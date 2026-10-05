const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');
const ts = require('typescript');

function deferred() {
  let resolve, reject;
  const promise = new Promise((yes, no) => { resolve = yes; reject = no; });
  return { promise, resolve, reject };
}

function setup() {
  const requests = [];
  const api = {
    user: { role: 'ADMIN' },
    get: () => {
      const request = deferred();
      requests.push(request);
      return request.promise;
    },
    post: async () => ({ id: 2, name: 'New account' }),
  };
  class Api {}
  class Router {}
  class ChangeDetectorRef {}
  const module = { exports: {} };
  const source = readFileSync(path.join(__dirname, '../application/frontend/src/workbench.ts'), 'utf8');
  const compiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, experimentalDecorators: true },
  }).outputText;
  vm.runInNewContext(compiled, {
    exports: module.exports,
    require: (name) => {
      if (name === '@angular/core') return {
        Directive: () => (type) => type,
        ChangeDetectorRef,
        inject: (type) => type === Api ? api : { markForCheck() {} },
      };
      if (name === '@angular/router') return { Router };
      if (name === './api') return { Api };
      if (name === './roles') return { roleLabel: (role) => role };
      throw new Error(name);
    },
  });
  const page = new module.exports.Workbench();
  page.config = { endpoint: 'users' };
  return { page, requests, api };
}

test('account creation refreshes even while an older list request is running', async () => {
  const { page, requests } = setup();
  const oldLoad = page.load(true);
  page.create();
  page.form.name = 'New account';
  page.save();
  await new Promise(setImmediate);
  assert.equal(requests.length, 2, 'saving must start a fresh list request');
  const updated = [{ id: 1 }, { id: 2, name: 'New account' }];
  requests[1].resolve(updated);
  await new Promise(setImmediate);
  assert.equal(page.rows, updated);
  assert.equal(page.modal, '');
  assert.equal(page.busy, false);
  assert.equal(page.notice, 'Saved successfully');
  requests[0].resolve([{ id: 1 }]);
  await oldLoad;
  assert.equal(page.rows, updated, 'stale response must not remove the new account');
});

test('an obsolete refresh error cannot replace a successful refresh', async () => {
  const { page, requests } = setup();
  const oldLoad = page.load();
  const newLoad = page.load(true, true);
  requests[1].resolve([{ id: 2 }]);
  await newLoad;
  requests[0].reject(new Error('Old request failed'));
  await oldLoad;
  assert.equal(page.error, '');
  assert.equal(page.loading, false);
});

test('failed account creation keeps the form available for correction', async () => {
  const { page, api, requests } = setup();
  api.post = async () => { throw new Error('Email already exists'); };
  page.create();
  page.save();
  await new Promise(setImmediate);
  assert.equal(page.modal, 'edit');
  assert.equal(page.error, 'Email already exists');
  assert.equal(page.busy, false);
  assert.equal(requests.length, 0);
});

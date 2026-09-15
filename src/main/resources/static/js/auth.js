/* auth.js */
const API = '';

/* ── Particles ─── */
(function(){
  const c=document.getElementById('particles');
  if(!c)return;
  const cols=['#6c5ce7','#f0a500','#a29bfe','#ffcc44','#00cec9'];
  for(let i=0;i<50;i++){
    const p=document.createElement('div'); p.className='particle';
    const sz=Math.random()*8+2;
    p.style.cssText=`width:${sz}px;height:${sz}px;left:${Math.random()*100}%;background:${cols[Math.floor(Math.random()*cols.length)]};animation-duration:${6+Math.random()*14}s;animation-delay:${Math.random()*12}s;`;
    c.appendChild(p);
  }
})();

function switchTab(tab){
  const isLogin=tab==='login';
  ['loginForm','registerForm'].forEach(id=>document.getElementById(id).classList.toggle('hidden',id==='loginForm'?!isLogin:isLogin));
  document.getElementById('loginTab').classList.toggle('active',isLogin);
  document.getElementById('registerTab').classList.toggle('active',!isLogin);
}

function togglePass(id,btn){
  const inp=document.getElementById(id);
  const show=inp.type==='text';
  inp.type=show?'password':'text';
  btn.querySelector('i').className=show?'fa-solid fa-eye':'fa-solid fa-eye-slash';
}

async function doLogin(e){
  e.preventDefault();
  const err=document.getElementById('loginError');
  err.textContent='';
  const btn=e.target.querySelector('.submit-btn');
  btn.disabled=true; btn.querySelector('span').textContent='Signing in…';
  try{
    const res=await fetch(`${API}/api/auth/login`,{
      method:'POST',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({email:document.getElementById('loginEmail').value,password:document.getElementById('loginPassword').value})
    });
    const data=await res.json();
    if(!res.ok) throw new Error(data.message||'Invalid email or password');
    localStorage.setItem('aams_user',JSON.stringify(data.data));
    routeToDashboard(data.data.role);
  }catch(ex){
    err.textContent=ex.message;
    btn.disabled=false; btn.querySelector('span').textContent='Sign In';
  }
}

async function doRegister(e){
  e.preventDefault();
  const err=document.getElementById('registerError');
  const ok=document.getElementById('registerSuccess');
  err.textContent=''; ok.textContent='';
  const btn=e.target.querySelector('.submit-btn');
  btn.disabled=true; btn.querySelector('span').textContent='Creating…';
  try{
    const res=await fetch(`${API}/api/auth/register`,{
      method:'POST',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({
        firstName:document.getElementById('regFirst').value,
        lastName:document.getElementById('regLast').value,
        email:document.getElementById('regEmail').value,
        phone:document.getElementById('regPhone').value,
        city:document.getElementById('regCity').value,
        postalCode:document.getElementById('regPostal').value,
        password:document.getElementById('regPassword').value
      })
    });
    const data=await res.json();
    if(!res.ok) throw new Error(data.message||'Registration failed');
    ok.textContent='Account created! Redirecting…';
    localStorage.setItem('aams_user',JSON.stringify(data.data));
    setTimeout(()=>routeToDashboard(data.data.role),1200);
  }catch(ex){
    err.textContent=ex.message;
    btn.disabled=false; btn.querySelector('span').textContent='Create Account';
  }
}

function routeToDashboard(role){
  const map={
    CUSTOMER:'/pages/useraccount/customer-dashboard.html',
    ADMIN:'/pages/useraccount/admin-dashboard.html',
    MANAGER:'/pages/useraccount/manager-dashboard.html',
    DESIGNER:'/pages/useraccount/designer-dashboard.html',
    MKT_MANAGER:'/pages/useraccount/mkt-manager-dashboard.html',
    SALES_REP:'/pages/useraccount/sales-dashboard.html',
    OFFICER:'/pages/communication/officer-dashboard.html'
  };
  window.location.href=map[role]||'/pages/useraccount/customer-dashboard.html';
}

function logout(){
  localStorage.removeItem('aams_user');
  window.location.href='/login.html';
}

(function checkSession(){
  const u=JSON.parse(localStorage.getItem('aams_user')||'null');
  if(u) routeToDashboard(u.role);
})();
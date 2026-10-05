/* Lightweight, dependency-free WebGL Earth for the public landing page. */
window.AxiomHero3D = (() => {
  let cleanup = null;
  const vertexSource = `
    attribute vec3 aPosition;
    attribute vec3 aNormal;
    uniform float uYaw;
    uniform float uPitch;
    uniform float uAspect;
    uniform float uTime;
    varying vec3 vPosition;
    varying vec3 vNormal;
    varying vec3 vObjectNormal;
    void main() {
      float cy=cos(uYaw), sy=sin(uYaw), cx=cos(uPitch), sx=sin(uPitch);
      mat3 ry=mat3(cy,0.,-sy, 0.,1.,0., sy,0.,cy);
      mat3 rx=mat3(1.,0.,0., 0.,cx,sx, 0.,-sx,cx);
      mat3 rotation=rx*ry;
      vec3 p=rotation*aPosition;
      vObjectNormal=aNormal;
      vPosition=p;
      vNormal=rotation*aNormal;
      float z=p.z-5.1;
      gl_Position=vec4(p.x*2.3/uAspect, p.y*2.3, -1.002002*z-.2002002, -z);
    }
  `;
  const fragmentSource = `
    precision mediump float;
    uniform sampler2D uMap;
    uniform float uTime;
    varying vec3 vPosition;
    varying vec3 vNormal;
    varying vec3 vObjectNormal;
    void main() {
      vec3 n=normalize(vNormal), local=normalize(vObjectNormal);
      vec2 uv=vec2((atan(local.x,local.z)+3.14159265)/6.2831853,(asin(clamp(local.y,-1.,1.))+1.5707963)/3.14159265);
      vec3 base=texture2D(uMap,uv).rgb;
      vec3 view=normalize(vec3(0.,0.,5.1)-vPosition);
      vec3 key=normalize(vec3(-2.,3.,5.));
      float light=.38+.68*max(dot(n,key),0.);
      float rim=pow(1.-max(dot(n,view),0.),3.);
      vec3 sri=vec3(0.977755659,0.136925898,0.158887287);
      float dist=acos(clamp(dot(local,normalize(sri)),-1.,1.));
      float dotGlow=exp(-dist*dist*1800.);
      float ring=exp(-pow((dist-(.055+.014*sin(uTime*2.)))*95.,2.));
      vec3 color=base*light+vec3(.28,.48,.67)*rim*.55;
      color+=vec3(1.,.32,.45)*(dotGlow*.9+ring*.55);
      color+=vec3(.21,.4,.53)*pow(max(dot(n,normalize(key+view)),0.),60.)*.25;
      gl_FragColor=vec4(pow(color,vec3(.87)),1.);
    }
  `;
  function geometry() {
    const vertices=[],indices=[],latitudes=64,longitudes=128;
    for(let i=0;i<=latitudes;i++){
      const lat=-Math.PI/2+i/latitudes*Math.PI;
      for(let j=0;j<=longitudes;j++){
        const lon=-Math.PI+j/longitudes*Math.PI*2;
        const n=[Math.cos(lat)*Math.sin(lon),Math.sin(lat),Math.cos(lat)*Math.cos(lon)];
        vertices.push(...n.map(x=>x*1.55),...n);
      }
    }
    for(let i=0;i<latitudes;i++)for(let j=0;j<longitudes;j++){
      const a=i*(longitudes+1)+j,b=a+longitudes+1;
      indices.push(a,b,a+1,b,b+1,a+1);
    }
    return {vertices:new Float32Array(vertices),indices:new Uint16Array(indices)};
  }
  function dispose(){if(cleanup){const fn=cleanup;cleanup=null;fn();}}
  function mount(){
    dispose();
    const host=document.getElementById('hero-3d'),canvas=document.getElementById('hero-3d-canvas');
    if(!host||!canvas)return;
    let gl;
    try {gl=canvas.getContext('webgl',{alpha:true,antialias:true,powerPreference:'low-power',premultipliedAlpha:false});} catch {return;}
    if(!gl)return;
    let program,buffer,indexBuffer,vs,fs,texture,mapImage,frame=0,disposed=false,observer,visibilityObserver;
    const removers=[];
    const listen=(target,event,fn,options)=>{if(!target)return;target.addEventListener(event,fn,options);removers.push(()=>target.removeEventListener(event,fn,options));};
    let yaw=-80.77*Math.PI/180,pitch=7.87*Math.PI/180,elapsed=0,last=0,dragging=false,lastX=0,lastY=0,inView=true;
    const media=window.matchMedia('(prefers-reduced-motion: reduce)');
    let running=!media.matches;
    const play=host.querySelector('[data-hero="play"]');
    const marker=host.querySelector('.earth-marker');
    function updatePlay(){if(!play)return;play.textContent=running?'Pause rotation':'Play rotation';play.setAttribute('aria-pressed',String(running));}
    function stop(){if(frame)cancelAnimationFrame(frame);frame=0;last=0;}
    function fail(){stop();host.classList.remove('hero3d-ready');canvas.tabIndex=-1;}
    try {
      function shader(type,source){const s=gl.createShader(type);gl.shaderSource(s,source);gl.compileShader(s);if(!gl.getShaderParameter(s,gl.COMPILE_STATUS)){gl.deleteShader(s);throw new Error('Shader unavailable');}return s;}
      vs=shader(gl.VERTEX_SHADER,vertexSource);fs=shader(gl.FRAGMENT_SHADER,fragmentSource);
      program=gl.createProgram();gl.attachShader(program,vs);gl.attachShader(program,fs);gl.linkProgram(program);
      if(!gl.getProgramParameter(program,gl.LINK_STATUS))throw new Error('Renderer unavailable');
      const mesh=geometry();
      buffer=gl.createBuffer();gl.bindBuffer(gl.ARRAY_BUFFER,buffer);gl.bufferData(gl.ARRAY_BUFFER,mesh.vertices,gl.STATIC_DRAW);
      indexBuffer=gl.createBuffer();gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER,indexBuffer);gl.bufferData(gl.ELEMENT_ARRAY_BUFFER,mesh.indices,gl.STATIC_DRAW);
      gl.useProgram(program);
      const position=gl.getAttribLocation(program,'aPosition'),normal=gl.getAttribLocation(program,'aNormal');
      gl.enableVertexAttribArray(position);gl.vertexAttribPointer(position,3,gl.FLOAT,false,24,0);
      gl.enableVertexAttribArray(normal);gl.vertexAttribPointer(normal,3,gl.FLOAT,false,24,12);
      const uniforms={};for(const n of ['uYaw','uPitch','uAspect','uTime'])uniforms[n]=gl.getUniformLocation(program,n);
      gl.enable(gl.DEPTH_TEST);gl.clearColor(0,0,0,0);
      texture=gl.createTexture();gl.bindTexture(gl.TEXTURE_2D,texture);
      gl.texImage2D(gl.TEXTURE_2D,0,gl.RGB,1,1,0,gl.RGB,gl.UNSIGNED_BYTE,new Uint8Array([15,26,43]));
      gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_MIN_FILTER,gl.LINEAR);
      gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_MAG_FILTER,gl.LINEAR);
      gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_WRAP_S,gl.REPEAT);
      gl.texParameteri(gl.TEXTURE_2D,gl.TEXTURE_WRAP_T,gl.CLAMP_TO_EDGE);
      gl.uniform1i(gl.getUniformLocation(program,'uMap'),0);
      mapImage=new Image();mapImage.onload=()=>{if(disposed)return;gl.bindTexture(gl.TEXTURE_2D,texture);gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL,true);gl.texImage2D(gl.TEXTURE_2D,0,gl.RGB,gl.RGB,gl.UNSIGNED_BYTE,mapImage);host.classList.add('hero3d-ready');canvas.tabIndex=0;resize();draw();schedule();};mapImage.onerror=fail;mapImage.src='earth-map.png';
      function draw(){
        if(disposed||gl.isContextLost())return;
        gl.clear(gl.COLOR_BUFFER_BIT|gl.DEPTH_BUFFER_BIT);
        gl.uniform1f(uniforms.uYaw,yaw);gl.uniform1f(uniforms.uPitch,pitch);
        gl.uniform1f(uniforms.uTime,elapsed);gl.uniform1f(uniforms.uAspect,canvas.width/canvas.height);
        gl.drawElements(gl.TRIANGLES,mesh.indices.length,gl.UNSIGNED_SHORT,0);
        if(marker){
          const lat=7.87*Math.PI/180,lon=80.77*Math.PI/180;
          const x=1.6*Math.cos(lat)*Math.sin(lon),y=1.6*Math.sin(lat),z=1.6*Math.cos(lat)*Math.cos(lon);
          const xx=Math.cos(yaw)*x+Math.sin(yaw)*z,zz=-Math.sin(yaw)*x+Math.cos(yaw)*z;
          const yy=Math.cos(pitch)*y-Math.sin(pitch)*zz,depth=Math.sin(pitch)*y+Math.cos(pitch)*zz;
          const visible=depth>1.55*1.55/5.1;
          marker.hidden=!visible;
          if(visible){marker.style.left=(50+xx*2.3/(canvas.width/canvas.height)/(5.1-depth)*50)+'%';marker.style.top=(50-yy*2.3/(5.1-depth)*50)+'%';}
        }
      }
      function tick(now){
        frame=0;if(disposed)return;
        if(!canvas.isConnected){dispose();return;}
        const delta=last?Math.min((now-last)/1000,.05):0;last=now;
        if(running&&!dragging){yaw+=delta*.065;elapsed+=delta;}
        draw();if(running&&inView&&!document.hidden)frame=requestAnimationFrame(tick);
      }
      function schedule(){if(!disposed&&running&&inView&&!document.hidden&&!frame){last=0;frame=requestAnimationFrame(tick);}}
      function resize(){
        const rect=canvas.getBoundingClientRect(),ratio=Math.min(window.devicePixelRatio||1,1.75);
        if(rect.width<=0||rect.height<=0)return;
        canvas.width=Math.round(rect.width*ratio);canvas.height=Math.round(rect.height*ratio);
        gl.viewport(0,0,canvas.width,canvas.height);draw();
      }
      listen(play,'click',()=>{running=!running;updatePlay();if(running)schedule();else stop();draw();});
      listen(host.querySelector('[data-hero="reset"]'),'click',()=>{yaw=-80.77*Math.PI/180;pitch=7.87*Math.PI/180;elapsed=0;draw();});
      listen(canvas,'dblclick',()=>{yaw=-80.77*Math.PI/180;pitch=7.87*Math.PI/180;elapsed=0;draw();});
      listen(canvas,'pointerdown',e=>{if(e.pointerType==='mouse'&&e.button!==0)return;dragging=true;lastX=e.clientX;lastY=e.clientY;canvas.setPointerCapture(e.pointerId);host.classList.add('hero3d-dragging');});
      listen(canvas,'pointermove',e=>{if(!dragging)return;yaw+=(e.clientX-lastX)*.009;if(e.pointerType==='mouse')pitch=Math.max(-1.2,Math.min(1.2,pitch+(e.clientY-lastY)*.006));lastX=e.clientX;lastY=e.clientY;draw();});
      const release=()=>{dragging=false;host.classList.remove('hero3d-dragging');};
      listen(canvas,'pointerup',release);listen(canvas,'pointercancel',release);listen(canvas,'lostpointercapture',release);
      listen(canvas,'keydown',e=>{if(['ArrowLeft','ArrowRight','ArrowUp','ArrowDown',' '].includes(e.key)){e.preventDefault();if(e.key==='ArrowLeft')yaw-=.15;if(e.key==='ArrowRight')yaw+=.15;if(e.key==='ArrowUp')pitch=Math.max(-1.2,pitch-.12);if(e.key==='ArrowDown')pitch=Math.min(1.2,pitch+.12);if(e.key===' '){running=!running;updatePlay();running?schedule():stop();}draw();}});
      listen(document,'visibilitychange',()=>document.hidden?stop():schedule());
      if(media.addEventListener)listen(media,'change',e=>{if(e.matches){running=false;stop();updatePlay();draw();}});
      listen(canvas,'webglcontextlost',e=>{e.preventDefault();fail();});
      if(window.ResizeObserver){observer=new ResizeObserver(resize);observer.observe(canvas);}else listen(window,'resize',resize);
      if(window.IntersectionObserver){visibilityObserver=new IntersectionObserver(entries=>{inView=entries[0].isIntersecting;if(inView)schedule();else stop();},{threshold:.05});visibilityObserver.observe(host);}
      updatePlay();resize();schedule();
      cleanup=()=>{disposed=true;stop();observer?.disconnect();visibilityObserver?.disconnect();removers.forEach(fn=>fn());if(mapImage){mapImage.onload=null;mapImage.onerror=null;}if(texture)gl.deleteTexture(texture);if(buffer)gl.deleteBuffer(buffer);if(indexBuffer)gl.deleteBuffer(indexBuffer);if(program)gl.deleteProgram(program);if(vs)gl.deleteShader(vs);if(fs)gl.deleteShader(fs);gl.getExtension('WEBGL_lose_context')?.loseContext();};
    } catch {
      fail();if(mapImage){mapImage.onload=null;mapImage.onerror=null;}if(texture)gl.deleteTexture(texture);if(buffer)gl.deleteBuffer(buffer);if(indexBuffer)gl.deleteBuffer(indexBuffer);if(program)gl.deleteProgram(program);if(vs)gl.deleteShader(vs);if(fs)gl.deleteShader(fs);removers.forEach(fn=>fn());observer?.disconnect();visibilityObserver?.disconnect();
    }
  }
  return {mount,dispose};
})();

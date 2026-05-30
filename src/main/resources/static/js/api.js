const BASE = '/api';

function getToken() { return localStorage.getItem('token'); }
function setToken(t) { localStorage.setItem('token', t); }
function clearToken() { localStorage.removeItem('token'); localStorage.removeItem('user'); }
function getUser() { try { return JSON.parse(localStorage.getItem('user')); } catch(e) { return null; } }
function setUser(u) { localStorage.setItem('user', JSON.stringify(u)); }
function isLoggedIn() { return !!getToken(); }
function isAdmin() { const u = getUser(); return u && u.role === 'admin'; }

async function api(method, path, body) {
    const headers = {};
    const token = getToken();
    if (token) headers['Authorization'] = token;
    if (body) headers['Content-Type'] = 'application/json';
    const opts = { method, headers };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(BASE + path, opts);
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || '请求失败');
    return data;
}

const $ = {
    get: (path) => api('GET', path),
    post: (path, body) => api('POST', path, body),
    put: (path, body) => api('PUT', path, body),
    del: (path) => api('DELETE', path)
};

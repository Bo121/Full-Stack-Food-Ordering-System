(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';

  // Create axios instance
  const service = axios.create({
    // The baseURL option in axios request configuration represents the common part of the request URL
    baseURL: '/',
    // Timeout
    timeout: 1000000
  });

  // Request interceptor
  service.interceptors.request.use(config => {
    // Whether to set token
    // const isToken = (config.headers || {}).isToken === false
    // if (getToken() && !isToken) {
    //   config.headers['Authorization'] = 'Bearer ' + getToken() // Let each request carry a custom token, please modify according to the actual situation
    // }

    // Map params parameters for GET requests
    if (config.method === 'get' && config.params) {
      let url = config.url + '?';
      for (const propName of Object.keys(config.params)) {
        const value = config.params[propName];
        var part = encodeURIComponent(propName) + "=";
        if (value !== null && typeof(value) !== "undefined") {
          if (typeof value === 'object') {
            for (const key of Object.keys(value)) {
              let params = propName + '[' + key + ']';
              var subPart = encodeURIComponent(params) + "=";
              url += subPart + encodeURIComponent(value[key]) + "&";
            }
          } else {
            url += part + encodeURIComponent(value) + "&";
          }
        }
      }
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    return config;
  }, error => {
    console.log(error);
    Promise.reject(error);
  });

  // Response interceptor
  service.interceptors.response.use(res => {
        console.log('---Response Interceptor---', res);
        // Default to success status if no status code is set
        const code = res.data.code;
        // Get error message
        const msg = res.data.msg;
        console.log('---code---', code);
        if (res.data.code === 0 && res.data.msg === 'NOTLOGIN') { // Redirect to login page
          console.log('---/backend/page/login/login.html---', code);
          localStorage.removeItem('userInfo');
          window.top.location.href = '/backend/page/login/login.html';
        } else {
          return res.data;
        }
      },
      error => {
        console.log('err' + error);
        let { message } = error;
        if (message == "Network Error") {
          message = "Backend interface connection exception";
        } else if (message.includes("timeout")) {
          message = "System interface request timeout";
        } else if (message.includes("Request failed with status code")) {
          message = "System interface " + message.substr(message.length - 3) + " exception";
        }
        window.ELEMENT.Message({
          message: message,
          type: 'error',
          duration: 5 * 1000
        });
        return Promise.reject(error);
      }
  );

  win.$axios = service;
})(window);

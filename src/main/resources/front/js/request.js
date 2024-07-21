(function (win) {
  axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
  // Create axios instance
  const service = axios.create({
    // In axios request configuration, baseURL indicates the common part of the request URL
    baseURL: '/',
    // Timeout
    timeout: 1000000
  })
  // Request interceptor
  service.interceptors.request.use(config => {
    // Whether token needs to be set
    // const isToken = (config.headers || {}).isToken === false
    // if (getToken() && !isToken) {
    //   config.headers['Authorization'] = 'Bearer ' + getToken() // Let each request carry a custom token, please modify according to actual situation
    // }
    // Mapping params parameters in get requests
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
    return config
  }, error => {
    Promise.reject(error)
  })

  // Response interceptor
  service.interceptors.response.use(res => {
        console.log('---Response Interceptor---', res)
        if (res.data.code === 0 && res.data.msg === 'NOTLOGIN') {// Return to login page
          window.top.location.href = '/front/page/login.html'
        } else {
          return res.data
        }
      },
      error => {
        let { message } = error;
        if (message == "Network Error") {
          message = "Backend interface connection exception";
        }
        else if (message.includes("timeout")) {
          message = "System interface request timeout";
        }
        else if (message.includes("Request failed with status code")) {
          message = "System interface " + message.substr(message.length - 3) + " exception";
        }
        window.vant.Notify({
          message: message,
          type: 'warning',
          duration: 5 * 1000
        })
        //window.top.location.href = '/front/page/no-wify.html'
        return Promise.reject(error)
      }
  )
  win.$axios = service
})(window);

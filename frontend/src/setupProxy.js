const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
  console.log("✅ setupProxy.js loaded"); 
  app.use(
    ["/login", "/user"],
    createProxyMiddleware({
      target: "http://localhost:8080",
      changeOrigin: true,
      logLevel: "debug", 
    })
  );
};
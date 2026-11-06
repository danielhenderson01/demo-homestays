function handler(event) {
  var request = event.request;
  var uri = request.uri;

  // Keep API paths untouched.
  if (uri === '/api' || uri.startsWith('/api/')) {
    return request;
  }

  // Let the SPA router handle paths that are not files.
  if (!uri.includes('.')) {
    request.uri = '/index.html';
  }

  return request;
}

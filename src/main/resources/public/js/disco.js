const url = `ws://${window.location.host}/disco`

const connection = new WebSocket(url);

connection.onopen = () => {
  console.log('--- connection onopen');
}

connection.onerror = err => {
  console.error('--- connection onerror', err);
}

connection.onmessage = e => {
  console.log('--- connection onmessage: ', e.data)
}

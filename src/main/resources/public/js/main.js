const url = `ws://${window.location.host}/midi`

const connection = new WebSocket(url);
let bandMember;

connection.onopen = () => {
  console.log('--- connection onopen');
}

connection.onerror = err => {
  console.error('--- connection onerror', err);
}

connection.onmessage = e => {
  console.log('--- connection onmessage: ', e.data)
  setUpBandmember(JSON.parse(e.data));
}

$('#musicButton').on("mousedown touchstart", () => {
  console.log('>>> mousedown touchstart')
  connection.send(JSON.stringify({ pressed: true }));
});

$('#musicButton').on("mouseup touchend", () => {
  console.log('>>> mouseup touchend')
  connection.send(JSON.stringify({ pressed: false }));
});

const setUpBandMember = bandMember => {
  const mB = $('#musicButton');
  mB.css('background-color': bandMember.color);
  mB.fadeIn();
}

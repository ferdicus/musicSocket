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
  setUpBandMember(JSON.parse(e.data));
}

$('#musicButton').on("touchstart", () => {
  connection.send(JSON.stringify({ pressed: true }));
});

$('#musicButton').on("touchend", () => {
  connection.send(JSON.stringify({ pressed: false }));
});

const setUpBandMember = bandMember => {
  const mB = $('#musicButton');
  const name = $('#name');
  mB.css({'backgroundColor': bandMember.color});
  name.text(bandMember.name);
  mB.fadeIn();
  name.fadeIn();
}

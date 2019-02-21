const url = `ws://${window.location.host}/midi`

const connection = new WebSocket(url);
let bandMember;
let note = '60';

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
  connection.send(JSON.stringify({
    pressed: true, note
  }));
});

$('#musicButton').on("touchend", () => {
  connection.send(JSON.stringify({
    pressed: false, note
  }));
});

$("#note").attr("value", note);
$('#note').on('change', e => {
  note = e.target.value;
});

const setUpBandMember = bandMember => {
  const mB = $('#musicButton');
  const name = $('#name');
  const instrument = $('#instrument');

  const container = $('#container');

  container.fadeIn();
  mB.css({'backgroundColor': bandMember.color});
  name.text(`You are: ${bandMember.name}`);
  instrument.text(`You play: ${bandMember.instrumentName}`);
  container.fadeIn();
}

const url = `ws://${window.location.host}/disco`

const connection = new WebSocket(url);
const memberTiles = $('#grid').children();

connection.onopen = () => {
  console.log('--- connection onopen');
};

connection.onerror = err => {
  console.error('--- connection onerror', err);
};

connection.onmessage = e => {
  const currentBandMembers = JSON.parse(e.data)
  console.log('--- connection onmessage: ', e.data);

  $.each(memberTiles, index => {
    $(this).css({'backgroundColor': currentBandMembers[index].color})
    if (currentBandMembers[index].playing) $(this).text('♫');
  });
};

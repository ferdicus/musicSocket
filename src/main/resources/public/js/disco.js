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
  const currentBandMembers = JSON.parse(e.data);
  console.log('--- connection onmessage: ', e.data);

  $.each(memberTiles, (index, tile) => {
    if (!currentBandMembers[index]) return false;
    $(tile).css({'backgroundColor': currentBandMembers[index].color})
    currentBandMembers[index].playing ? $(tile).text('♫') : $(tile).text('');
  });
};

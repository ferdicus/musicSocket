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
  const currentBandMembers = e.data
  console.log('--- connection onmessage: ', e.data);

  if (currentBandMembers) {
    $.each(memberTiles, (index, tile) => {
      if (!currentBandMembers[index]) return false;
      $(tile).css({'backgroundColor': currentBandMembers[index].color})
      if (currentBandMembers[index].playing) $(tile).text('♫');
    });
  }
};

var critical = require('critical');

critical.generate({
    inline: true,
    base: '.',
    src: 'index-src.html',
    dest: 'index.html',
    width: 1300,
    height: 900
});
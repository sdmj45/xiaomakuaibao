$( "ul.timeline" ).load( "assets/data/news.html", function(){
    var list = $('ul.timeline');
    var listItems = list.children('li');
    list.append(listItems.get().reverse());
} );
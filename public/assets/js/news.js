$( "ul.timeline" ).load( "assets/js/data/news.html", function(){
    var list = $('ul.timeline');
    var listItems = list.children('li');
    list.append(listItems.get().reverse());
} );

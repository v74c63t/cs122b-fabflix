let movie_search_form = $("#movie-search-form");

function htmlHREF(html_page, id, name) {
    return '<a style="color:darkturquoise;" href="' + html_page + '.html?id=' + id + '">' +
        name +     // display star_name for the link text
        '</a>';
}

function handleGenreResult(resultData) {
    console.log("handleResult: populating movies info from resultData");
    console.log(resultData);

    let genreList = jQuery("#genre-list");

    for (let i = 0; i < resultData.length; i++) {
        let genre = resultData[i]["genre_name"];
        let genreId = resultData[i]['genre_id'];
        let listHTML = "<h4><a style='color: #ffc107;' href='result.html?genreId=" + genreId +'&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0' +  "'>" + genre + "</a></h4>";
        // add in the href later
        genreList.append(listHTML);
    }
}

function handleTitle() {
    console.log('handling title');
    let alphanumList = jQuery("#alphanum-list");
    let alpha = 'A';
    for(let i = 0; i < 26; i++){
        let alphaHTML = "<h4><a style='color: #ffc107;' href='result.html?startTitle=" + alpha + '&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0' +"'>" + alpha + '</a></h4>';
        alpha = String.fromCharCode(alpha.charCodeAt(0) + 1);
        alphanumList.append(alphaHTML);
    }
    // let numList = jQuery("#num-list");
    alphanumList.append("<br>")
    let num = '0';
    for(let i = 0; i < 10; i++){
        let numHTML = "<h4><a style='color: #ffc107;' href='result.html?startTitle=" + num + '&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0' +"'>" + num + '</a></h4>';
        num = String.fromCharCode(num.charCodeAt(0) + 1);
        alphanumList.append(numHTML);
    }
    let numHTML = "<h4><a style='color: #ffc107;' href='result.html?startTitle=*&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0'>*</a></h4>";
    alphanumList.append(numHTML);
}
function handleInit(resultData) {
    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }
    handleGenreResult(resultData);
    handleTitle();
}

function handleSearch(searchSubmitEvent) {
    let paramArray = []
    $(".search-item").each( function(i, e) {
        if ($(this)[0].value != "") {
            paramArray.push([$(this)[0].name, $(this)[0].value]);
        }
    })

    searchSubmitEvent.preventDefault();

    let url = "";
    for (let i = 0; i < paramArray.length; i++) {
        if (i == paramArray.length-1) {
            url += paramArray[i][0] + "=" + paramArray[i][1];
            url += "&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0";
        } else {
            url += paramArray[i][0] + "=" + paramArray[i][1] + "&";
        }
    }
    window.location.replace("result.html?" + url);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/maininit", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleInit(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});

// Binds submit action to handleSearch handler function
movie_search_form.submit(handleSearch);
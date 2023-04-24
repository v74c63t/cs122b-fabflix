
let movie_search_form = $("#movie-search-form");
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}
// To be implemented unless there are changes
function getURLParams(paramsObj) {
    let map = Object.entries(paramsObj)
    let urlString = ''
    for (let keyVal of map) {
        urlString += (keyVal[0] + '=' + keyVal[1]) + '&';
    }
    urlString = urlString.substring(0,urlString.length-1);
    // urlString += "&numRecords=25&firstRecord=0";
    return urlString
}

// To handle all hyperlinks
function htmlHREF(html_page, id, name) {
    return '<a style="color:darkturquoise;" href="' + html_page + '.html?id=' + id + '">' +
        name +     // display star_name for the link text
        '</a>';
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
            url += "&numRecords=25&firstRecord=0";
        } else {
            url += paramArray[i][0] + "=" + paramArray[i][1] + "&";
        }
    }
    window.location.replace("result.html?" + url);
}

function movieSettingsSetup(){
    if(getParameterByName('genreId')) {
        $("#genreId").attr('name', 'genreId');
        $("#genreId").attr('value', getParameterByName('genreId'));
    }
    if(getParameterByName('startTitle')) {
        $("#startTitle").attr('name', 'startTitle');
        $("#startTitle").attr('value', getParameterByName('startTitle'));
    }
    if(getParameterByName('title')) {
        $("#title").attr('name', 'title');
        $("#title").attr('value', getParameterByName('title'));
    }
    if(getParameterByName('year')) {
        $("#year").attr('name', 'year');
        $("#year").attr('value', getParameterByName('year'));
    }
    if(getParameterByName('director')) {
        $("#director").attr('name', 'director');
        $("#director").attr('value', getParameterByName('director'));
    }
    if(getParameterByName('star')) {
        $("#star").attr('name', 'star');
        $("#star").attr('value', getParameterByName('star'));
    }
    document.getElementById("sortBy").value = getParameterByName('sortBy');
    document.getElementById("numRecords").value = getParameterByName('numRecords');
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: movie info from resultData");
    console.log(resultData)
    movieSettingsSetup();
    let url = window.location.href;
    // assuming that firstRecord would always be the last parameter will adjust if not the case later
    let parsed = url.substring(0,url.indexOf('firstRecord'));
    let prev = jQuery("#prev");
    let next = jQuery("#next");
    let numRecords = parseInt(getParameterByName("numRecords"));
    let firstRecord = parseInt(getParameterByName("firstRecord"));
    let nextHref = '';
    let prevHref = '';
    if(firstRecord-numRecords < 0){
        if(firstRecord != 0) {
            prevHref = parsed + 'firstRecord=' + (firstRecord-numRecords).toString();
            prev.attr("href", prevHref);
        }
    }
    else {
        prevHref = parsed + 'firstRecord=' + (firstRecord-numRecords).toString();
        prev.attr("href", prevHref);
    }
    if(resultData.length >= numRecords){
        console.log(resultData[0]["max_records"]);
        if (firstRecord+numRecords < parseInt(resultData[0]["max_records"])) {
            nextHref = parsed + 'firstRecord=' + (firstRecord+numRecords).toString();
            next.attr("href", nextHref);
        }

    }
    let movieTableBodyElement = jQuery("#movies_table_body");
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let starsArray = resultData[i]["movie_stars"].split(", ");
        let rowHTML = "";
        rowHTML += "<tr>"
        rowHTML +=
            "<th>" +
            htmlHREF("single-movie", resultData[i]["movie_id"], resultData[i]["movie_title"]) +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        let genresArray = resultData[i]["movie_genres"].split(", ");
        rowHTML += "<th>";
        for(let genres in genresArray) {
            let genre = genresArray[genres].split("|");
            rowHTML += "<a style='color:darkturquoise;' href='result.html?genreId=" + genre[0] + '&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0' + "'>" + genre[1] + "</a>, ";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-3);
        rowHTML += "</th>";
        rowHTML += "<th>";

        // iterate through stars to link star names to their respective single star page
        for (let stars in starsArray) {
            let starsArr = starsArray[stars].split("|");
            rowHTML += htmlHREF("single-star", starsArr[0], starsArr[1]) + ", ";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-3);
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] +
            " <i class='fa-sharp fa-solid fa-star' style='color: #ffd747;'></i></th>"
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}



/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let urlRequest = '';
let servletUrl = '';
if ( getParameterByName('genreId') ) {
    urlRequest = 'genreId=' + getParameterByName('genreId')+ '&sortBy=' + getParameterByName('sortBy') + '&numRecords='+getParameterByName('numRecords')+'&firstRecord='+getParameterByName('firstRecord');
    servletUrl += 'api/by-genre?';
}else if ( getParameterByName('startTitle') ) {
    urlRequest = 'startTitle=' + getParameterByName('startTitle') + '&sortBy=' + getParameterByName('sortBy') + '&numRecords='+getParameterByName('numRecords')+'&firstRecord='+getParameterByName('firstRecord');
    servletUrl += 'api/by-start-title?';
}else {
    let obj = {}
    if ( getParameterByName('title') ) { obj['title'] = getParameterByName('title'); }
    if ( getParameterByName('director') ) { obj['director'] = getParameterByName('director'); }
    if ( getParameterByName('year') ) { obj['year'] = getParameterByName('year'); }
    if ( getParameterByName('star') ) { obj['star'] = getParameterByName('star'); }
    obj['sortBy'] = getParameterByName('sortBy');
    obj['numRecords'] = getParameterByName('numRecords');
    obj['firstRecord'] = getParameterByName('firstRecord');
    urlRequest = getURLParams(obj);
    servletUrl += 'api/by-search?';
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: servletUrl + urlRequest, // Setting request url, which is mapped by SingleMovieServlet
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});

// Binds submit action to handleSearch handler function
movie_search_form.submit(handleSearch);

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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");

    // Set the result href to the most recent result url requested
    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }

    let movieTitle = jQuery("#movie_title");
    movieTitle.append(resultData[0]["movie_title"]);
    let movieInfoElement = jQuery("#movie_info");

    movieInfoElement.append("<div style='padding-left: 80px;'><i><strong>" + resultData[0]["movie_title"] + "</strong></i><span class='page-title-sub-info'> (" +
         + resultData[0]["movie_year"]+ ")</span></div>");

    console.log("handleResult: populating star table from resultData");

    // Populate table
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let starsArray = resultData[i]["movie_stars"].split(", ");
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        // rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
        let genresArray = resultData[i]["movie_genres"].split(", ");
        rowHTML += "<th>";
        for(let genres in genresArray) {
            let genre = genresArray[genres].split("|");
            rowHTML += "<a style='color:darkturquoise;' href='result.html?genreId=" + genre[0] + '&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0' +"'>" + genre[1] + "</a>, ";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-3);
        rowHTML += "</th>";
        rowHTML += "<th>";
        for (let stars in starsArray) {
            let starsArr = starsArray[stars].split("|");
            rowHTML += "<a style='color:darkturquoise;' href='single-star.html?id=" + starsArr[0]
                + "'>" + starsArr[1] + "</a>" + ", ";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-3);
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] +
                " <i class='fa-sharp fa-solid fa-star' style='color: #ffd747;'></i></th>";
        rowHTML += "<th><button type='submit' id='add_to_cart' style='font-family: Verdana, serif;color:darkturquoise;border-color:darkturquoise;' class='btn btn-secondary'>Add</button></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by SingleMovieServlet
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});

// Binds submit action to handleSearch handler function
movie_search_form.submit(handleSearch);

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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    console.log("handleResult: populating star info from resultData");

    let movieTitle = jQuery("#movie_title");
    movieTitle.append(resultData[0]["movie_title"]);
    let movieInfoElement = jQuery("#movie_info");

    console.log("SOMETHING", resultData[0]["movie_title"]);
    movieInfoElement.append("<div style='padding-left: 80px;'><i><strong>" + resultData[0]["movie_title"] + "</strong></i><span class='page-title-sub-info'> (" +
         + resultData[0]["movie_year"]+ ")</span></div>");

    console.log("handleResult: populating movie table from resultData");
    console.log(resultData);

    // Populate table
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let starsArray = resultData[i]["movie_stars"].split(", ");
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>";
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
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

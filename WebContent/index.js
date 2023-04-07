
// To handle all hyperlinks
function htmlHREF(html_page, id, name) {
    return '<a href=' + html_page + '.html?id=' + id + '">'
        + name +
        '</a>';
}

function handleMovieResult(resultData) {
    let moviesTableBodyElem = jQuery("#movies_table_body");

    // add rest
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let starsArray = resultData[i]["movie_stars"].split(", ");
        let rowHTML = "";
        rowHTML +="<tr>"
        rowHTML +=
            "<th>" +
            htmlHREF("single-movie", resultData[i]["movie_id"],resultData[i]["movie_title"]) +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>"
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>"
        // rowHTML += "<th>" + resultData[i]["movie_stars"] + "</th>"
        rowHTML += "<th>"

        for (let stars in starsArray) {
            let starsArr = starsArray[stars].split("|");
            rowHTML += htmlHREF("single-star", starsArr[0], starsArr[1]) + ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>"
        rowHTML += "</tr>";

        moviesTableBodyElem.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});


// To handle all hyperlinks
function htmlHREF(html_page, id, name) {
    return '<a style="color:darkturquoise;" href="' + html_page + '.html?id=' + id + '">' +
        name +     // display star_name for the link text
        '</a>';
}

function handleMovieResult(resultData) {
    console.log("handleResult: populating movies info from resultData");

    let moviesTableBodyElem = jQuery("#movies_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let starsArray = resultData[i]["movie_stars"].split(", ");
        let rowHTML = "";
        rowHTML += "<tr>"
        rowHTML +=
            "<th>" +
            htmlHREF("single-movie", resultData[i]["movie_id"], resultData[i]["movie_title"]) +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>"
        rowHTML += "<th>" + resultData[i]["movie_genres"] + "</th>"
        rowHTML += "<th>"

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

        moviesTableBodyElem.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

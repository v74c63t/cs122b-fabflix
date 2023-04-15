// TO DO
function htmlHREF(html_page, id, name) {
    return '<a style="color:darkturquoise;" href="' + html_page + '.html?id=' + id + '">' +
        name +     // display star_name for the link text
        '</a>';
}

function handleGenreResult(resultData) {
    console.log("handleResult: populating genres info from resultData");
    console.log(resultData)

    let genreListBodyElem = jQuery("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genresArray = resultData[i]["genres"].split(", ");
        let rowHTML = "";
        rowHTML += "<h4>"
        rowHTML += "TEST"

        // iterate through stars to link star names to their respective single star page
        for (let stars in starsArray) {
            let starsArr = starsArray[stars].split("|");
            rowHTML += htmlHREF("single-star", starsArr[0], starsArr[1]) + ", ";
        }
        rowHTML = rowHTML.substring(0,rowHTML.length-3);
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] +
            " <i class='fa-sharp fa-solid fa-star' style='color: #ffd747;'></i></th>"
        rowHTML += "</h4>";

        moviesTableBodyElem.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});
function handleGenreResult(resultData) {
    console.log("handleResult: populating movies info from resultData");

    let genreList = jQuery("#genre_list");

    for (let i = 0; i < resultData.length; i++) {
        let genre = resultData[i]["genre_name"];
        let listHTML = "<h4>" + genre + "</h4>";
        // add in the href later
        genreList.append(listHTML);
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/maininit", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});
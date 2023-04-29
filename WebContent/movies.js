
let movie_search_form = $("#movie-search-form");
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
            url += "&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0";
        } else {
            url += paramArray[i][0] + "=" + paramArray[i][1] + "&";
        }
    }
    window.location.replace("result.html?" + url);
}

function handleMovieResult(resultData) {
    console.log("handleResult: populating movies info from resultData");

    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }

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
        rowHTML += "<th><button type='submit' style='font-family: Verdana, serif;color:darkturquoise;border-color:darkturquoise;' class='btn btn-secondary' onclick=\"handleCart("+ "'" + resultData[i]["movie_id"] + "'" + ")\">Add</button></th>";
        rowHTML += "</tr>";

        moviesTableBodyElem.append(rowHTML);
    }
}

function handleCart(movieId) {

    $.ajax("api/cart", {
        method: "POST",
        data: {item: movieId, quantity: 1.0},
        success: resultDataString => {
            // let resultDataJson = JSON.parse(resultDataString);
            // console.log(resultDataJson);
            // console.log(resultDataJson[0]["key"], resultDataJson[0]["value"]["price"],resultDataJson[0]["value"]["quantity"])
            alert("Successfully added to cart");
        }
    })

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});

movie_search_form.submit(handleSearch);

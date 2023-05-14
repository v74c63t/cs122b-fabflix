
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
// note: have to modify and adjust to use code
function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // Set the result href to the most recent result url requested
    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starName = jQuery("#star_name");
    starName.append(resultData[0]["star_name"])
    let starInfoElement = jQuery("#star_info");

    if(resultData[0]["star_dob"] != null){
        starInfoElement.append("<div style='padding-left: 80px;'><i><strong>" + resultData[0]["star_name"] + "</strong></i><span class='page-title-sub-info'> (" +
            + resultData[0]["star_dob"] + ")</span></div>");
    }
    else{
        starInfoElement.append("<div style='padding-left: 80px;'><i><strong>" + resultData[0]["star_name"] + "</strong></i><span style='font-size: 24px'> (" +
            "N/A)</span></div>");
    }

    console.log("handleResult: populating movie table from resultData");

    // Populate table
    let starMovieTableBodyElement = jQuery("#star_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        if(resultData[i]["movie_id"] != null) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + "<a style='color:darkturquoise;' href='single-movie.html?id=" + resultData[i]["movie_id"]
                + "'>" + resultData[i]["movie_title"] + "</a></th>";
            rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
            rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
            rowHTML += "<th><button type='submit' id='add_to_cart' style='font-family: Verdana, serif;color:darkturquoise;border-color:darkturquoise;' class='btn btn-secondary' onclick=\"handleCart(" + "'" + resultData[i]["movie_id"] + "'" + ")\">Add</button></th>";
            rowHTML += "</tr>";
            // Append the row created to the table body, which will refresh the page
            starMovieTableBodyElement.append(rowHTML);
        }
    }
}

function handleCart(movieId) {

    $.ajax("api/cart", {
        method: "POST",
        data: {item: movieId, quantity: 1.0},
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            console.log(resultDataJson);
            console.log(resultDataJson[0]["key"], resultDataJson[0]["value"]["price"],resultDataJson[0]["value"]["quantity"])
            alert("Successfully added to cart");
        }
    })

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by SingleStarServlet
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

// Binds submit action to handleSearch handler function
movie_search_form.submit(handleSearch);

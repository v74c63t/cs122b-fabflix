
let movie_search_form = $("#movie-search-form");

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
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleConfirmationArray(resultData) {
    if(resultData[0]["resultUrl"] != null) {
        let resultTab = jQuery("#result");
        resultTab.attr("href", "result.html?" + resultData[0]["resultUrl"]);
    }
    console.log(resultData);
    let confirmationTableBody = $("#confirmation_table_body");
    // change it to html list
    // let rowHTML = "<ul>";
    let totalPrice = 0;
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr>";
        rowHTML += "<th>" + resultData[i]['saleId'].toString() + "</th>";
        rowHTML += "<th>" + resultData[i]['movie_title'] + "</th>";
        rowHTML += "<th>" + resultData[i]['movie_quantity'] + "</th>";
        rowHTML += "<th>$" + resultData[i]['movie_price'] + "</th>";
        totalPrice += (parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price']));
        rowHTML += "<th>$" + (parseInt(resultData[i]['movie_quantity']) * parseFloat(resultData[i]['movie_price'])).toString() + "</th>";
        rowHTML += '</tr>';
        confirmationTableBody.append(rowHTML);
    }
    document.getElementById("total").innerText = "Total Price: $" + totalPrice.toString();
}

$.ajax("api/confirmation", {
    method: "GET",
    success: handleConfirmationArray
});


movie_search_form.submit(handleSearch);

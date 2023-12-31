/* =========================  Handle Autocomplete ========================= */

const handleLookup = (query, doneCallback) => {
    console.log("Autocomplete initiated");
    if (typeof(Storage) !== "undefined") {
        if (localStorage.getItem(query) === null) {
            console.log("Sending ajax request to backend servlet");
            jQuery.ajax({
                "method": "GET",
                // generate the request url from the query.
                // escape the query string to avoid errors caused by special characters
                "url": "autocomplete?query=" + encodeURIComponent(query),
                "success": function(data) {
                    // pass the data, query, and doneCallback function into the success handler
                    handleLookupAjaxSuccess(data, query, doneCallback)
                },
                "error": function(errorData) {
                    console.log("lookup ajax error")
                    console.log(errorData)
                }
            })
        } else {
            // Use suggestions from localStorage
            console.log("Using cached results");
            handleLookupAjaxSuccess(localStorage.getItem(query), query, doneCallback)
        }
    } else {
        // Web Storage isn't supported
    }
};

const handleLookupAjaxSuccess = (data, query, doneCallback) => {
    console.log("Ajax successful");

    // Parse the string into JSON
    let jsonData = JSON.parse(data);
    console.log(jsonData);

    // Store query and suggestions in localStorage
    if (localStorage.getItem(query) === null) {
        localStorage.setItem(query, data);
    }
    doneCallback( { suggestions: jsonData } );
}

const handleSelectSuggestion = (suggestion) => {
    // console.log("You have selected " + suggestion["value"]);
    // console.log("single-movie page: single-movie.html?id=" + suggestion["data"]["movieId"]);
    window.location.replace("single-movie.html?id=" + suggestion["data"]["movieId"]);
}

$('#full-text').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: (query, doneCallback) => {
        handleLookup(query, doneCallback)
    },
    onSelect: (suggestion) => {
        handleSelectSuggestion(suggestion)
    },
    minChars: 3,
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

/* =========================  Handle Search ========================= */
const handleNormalSearch = (query) => {

    let url =  "query=" + query + "&sortBy=title+ASC+rating+ASC&numRecords=25&firstRecord=0";
    window.location.replace("result.html?" + url);
}

$('#full-text').keypress((event) => {
    if (event.keyCode == 13) {
        event.preventDefault();
        handleNormalSearch($("#full-text").val());
    }
})
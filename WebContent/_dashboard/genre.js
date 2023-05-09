let details_form = $("#new-details-form");


function handleAdd(resultData) {
    console.log("TEMP");
    let add_message = $("#add-message");
    add_message.text(resultData["message"]);
}
function handleDetails(submitEvent) {
    let details = {};

    $(".input-box").each( function(i, e) {
        details[$(this)[0].name] = $(this)[0].value;
    })

    submitEvent.preventDefault();

    console.log(details);

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        data: details,
        url: "api/add-genre", // Setting request url, which is mapped by MainInitServlet
        success: (resultData) => handleAdd(resultData) // Setting callback function to handle data returned successfully by the MainInitServlet
    });

}
// Binds submit action to handleAdd handler function
details_form.submit(handleDetails);

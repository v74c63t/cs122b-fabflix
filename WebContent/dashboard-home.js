function handleMetadata(resultData) {
    console.log("PLACEHOLDER")
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/metadata", // Setting request url, which is mapped by MainInitServlet
    success: (resultData) => handleMetadata(resultData) // Setting callback function to handle data returned successfully by the MainInitServlet
});
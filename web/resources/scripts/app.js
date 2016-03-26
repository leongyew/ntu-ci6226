(function () {
    $(function () {
        var query = $("#query").val();
        if (query || query.trim().length > 0) {
            $(".results").highlight(query, {
                caseSensitive: false
            });
        }
    });
})();
/**
 * Created by valeri on 20.01.15.
 */
$(function () {
    $(".language").click(function (event) {
        var language = $(event.target).data("language");
        document.cookie = "language=" + language;
        window.location.href = window.location.href.split("#")[0];
        return false;
    });
});
/*
 $(document).ready(function() {
 //toggle `popup` / `inline` mode
 $.fn.editable.defaults.mode = 'popup';

 //make username editable
 $('#username').editable();

 //make status editable
 $('#status').editable({
 type: 'select',
 title: 'Select status',
 placement: 'right',
 value: 2,
 source: [
 {value: 1, text: 'status 1'},
 {value: 2, text: 'status 2'},
 {value: 3, text: 'status 3'}
 ]

 //uncomment these lines to send data on server
 ,pk: 1
 ,url: '/post'
 });
 });
 */

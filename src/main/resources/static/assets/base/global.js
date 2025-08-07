$(function () {
//--------------------------------------//
//---------- GLOBAL FUNCTIONS ----------//
//--------------------------------------//
  if (window.$) {
    window.$.show_loading = function (obj_name = 'html') {
//      const block_ele = $(obj_name);
//      $(block_ele).block({
//        message: '<div class="ft-refresh-cw icon-spin font-medium-2"></div>',
//        overlayCSS: {
//          backgroundColor: '#fff',
//          opacity: 0.8,
//          cursor: 'wait'
//        },
//        css: {
//          border: 0,
//          padding: 0,
//          backgroundColor: 'transparent'
//        }
//      });
    };

    window.$.hide_loading = function (obj_name = 'html') {
//      $(obj_name).unblock();
    };

    window.$.meta = function (name) {
      if ($('meta[name=' + name + ']').length) {
        var base_context_path = $('meta[name=' + name + ']').attr("content");
        return base_context_path.substr(0, base_context_path.length);
      }
      return null;
    };

    window.$.context = function (URI = '', name = 'path') {
      if ($('meta[name=' + name + ']').length) {
        var base_context_path = $('meta[name=' + name + ']').attr("content");
        return base_context_path.substr(0, base_context_path.length - 1) + URI;
      }
      return null;
    };

    window.$.setCookie = function (name, value, days) {
      var expires = "";
      if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
      }
      document.cookie = name + "=" + (value || "") + expires + "; path=/";
    };

    window.$.getCookie = function (name) {
      var nameEQ = name + "=";
      var ca = document.cookie.split(';');
      for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ')
          c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0)
          return c.substring(nameEQ.length, c.length);
      }
      return null;
    };

    window.$.eraseCookie = function (name) {
      document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    };
  }
});
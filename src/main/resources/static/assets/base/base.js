$(function () {
//--------------------------------------//
//------------ BASE SCRIPTS ------------//
//--------------------------------------//
  const regexpath = new RegExp($.meta('regexpath'));

  function init() {
    $.show_loading();

    setTimeout(function () {
      if (!regexpath.test(window.location.href)) {
        $("[href]").each(function () {
          if (this.href === window.location.href) {
            $(this).parent('li').addClass("active");
            $(this).parent('li').parent('ul').children('li').addClass("is-shown");
            $(this).parent('li').parent('ul').parent('li').addClass("open");
            return true;
          }
        });
      }
      $.hide_loading();
    }, 10);
  }

  init();
});
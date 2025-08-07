var context = "";
var btn_saving_html = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>Saving...';
var btn_validating_html = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>Validating...';

$(function () {
  context = $.context();
  
  initMobileMask()
  initTelephoneMask()
  
  var module = $(location).attr("pathname").split('/')[2],
          module2 = $(location).attr("pathname").split('/').length > 3 ? $(location).attr("pathname").split('/')[3] : $(location).attr("pathname").split('/')[2];
  // .replace(/(etrade|\/)/g, "")
  // if ($(location).attr("pathname") == window.context + "/online-release") { // Hide sidebar
  //     $('body').attr('data-leftbar-size', 'condensed')
  // }

  /**
   * SWAL button styling
   */
  window.swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-primary mr-2',
      cancelButton: 'btn btn-outline-secondary'
    },
    buttonsStyling: false
  });
  $(document).ajaxStart(function () { // On ajax start, disable the submit buttons
    $('form').find("button[type=submit]:not(.skip-submit-loading)")
            .prop("disabled", true);
    // .html('<span class="spinner-grow spinner-grow-sm" role="status" aria-hidden="true"></span>');
  }).ajaxError(function () {
    $('form').find("button[type=submit]:not(.skip-submit-loading)")
            .prop("disabled", false);
  }).ajaxSuccess(function () {
// setTimeout( function() {
//     $('form').find("button[type=submit]:not(.skip-submit-loading)").prop("disabled", false);
//     // .html("Save");
// }, 1000);
  });
  if (sessionStorage.getItem("selection_origin") != module2) { // For each modules
    sessionStorage.removeItem("selection_response");
    sessionStorage.setItem("selection_origin", module2);
  }

  if ($("#form_search").length > 0) {
    $("#btnClear").on('click', function () {
      resetForm($("#form_search"));
    });
    if (sessionStorage.getItem("search_selection_origin") != "search-" + module) { // For each modules' search page
      sessionStorage.removeItem("search_selection_response");
      sessionStorage.setItem("search_selection_origin", "search-" + module);
    }

    initSelectionList();
    function initSelectionList()
    {
      // showLoader();

      if (sessionStorage.getItem("search_selection_response")) {
        disableSaveButtons();
        resetAndSetSelectionValue();
      } else {
        showLoaderInSelections();
        $.get(context + '/ajax/get/selection', $.param({
          get_importers: true,
//          get_statuses: $("#statusId").length > 0 ? true : false,
//          get_brokers: true,
//          get_lodgement_types: true,
//          get_boc_statuses: true,
//          get_olrs_statuses: $("#olrsStatusId").length > 0 ? true : false,
          get_ports: true,
//          get_release_types: true,
//          get_commodity_types: $("#commodityTypeId").length > 0 ? true : false,
//          get_customs_office: $("#customsOfficeId").length > 0 ? true : false,
//          get_manifest_statuses: $("#manifestStatusId").length > 0 ? true : false,
        }), function (response) {
          if (typeof response !== "object") {
            response = JSON.parse(response);
          }

          if (response.success) { // Append selection 
            // showLoader();

            sessionStorage.setItem("search_selection_response", JSON.stringify(response));
            resetAndSetSelectionValue();
          }
        }).fail(function () {
          showErrorMsg();
          enableSaveButtons();
        }).always(function () {

        });
      }
    }

    /**
     * Reset selection list
     */
    function resetAndSetSelectionValue()
    {
// showLoader();

// Remove options except the default 0
      $('option', $('select[name!=form_searchTbl_length]:not(.skip-dropdown-reset)')).not('[value=0]').remove();
      showLoaderInSelections();
      setTimeout(function () {
        appendSelections();
        setSelectionValue();
        setTimeout(function () {
          enableSaveButtons();
          hideLoaderInSelections();
          hideLoader();
        }, 500);
      }, 500);
    }

    /**
     * Append list
     */
    function appendSelections()
    {
      var selection_response = JSON.parse(sessionStorage.getItem("search_selection_response"));
      // Append options

      if ($("select#bocStatusId").length > 0) {
        $('select#bocStatusId').append(selection_response.boc_statuses_list);
      }
      if ($("select#brokerId").length > 0) {
        $('select#brokerId').append(selection_response.broker_list);
      }
      if ($("select#consigneeId").length > 0) {
        $('select#consigneeId').append(selection_response.importer_list);
      }
      if ($("select#commodityTypeId").length > 0) {
        $('select#commodityTypeId').append(selection_response.commodity_type_list);
      }
      if ($("select#companyCode").length > 0) {
        $('select#companyCode').append(selection_response.broker_list);
      }
      if ($("select#customsOfficeId").length > 0) {
        $('select#customsOfficeId').append(selection_response.customs_office_list);
      }
      if ($("select#importerId").length > 0) {
        $('select#importerId').append(selection_response.importer_list);
      }
      if ($("select#exporterId").length > 0) {
        $('select#exporterId').append(selection_response.importer_list);
      }
      if ($('select#lodgementTypeId').length > 0) {
        $('select#lodgementTypeId').append(selection_response.lodgement_list);
      }
      if ($("select#manifestStatusId").length > 0) {
        $('select#manifestStatusId').append(selection_response.manifest_statuses_list);
      }
      if ($("select#olrsStatusId").length > 0) {
        $('select#olrsStatusId').append(selection_response.olrs_statuses_list);
      }
      if ($("select#portCode").length > 0) {
        $('select#portCode').append(selection_response.ports_list);
      }
      if ($("select#releasedTypeId").length > 0) {
        $('select#releasedTypeId').append(selection_response.release_type_list);
      }
      if ($("select#statusId").length > 0) {
        $('select#statusId').append(selection_response.statuses_list);
      }
    }

    /**
     * Set selection value
     * @param {*} type 
     */
    function setSelectionValue()
    {
      const company_code_id = $('#companyCodeInp').val() ? $('#companyCodeInp').val() : 0;
      const importer_id = $('#importerIdInp').val() ? $('#importerIdInp').val() : 0;
      const broker_id = $('#brokerIdInp').val() ? $('#brokerIdInp').val() : 0;
      const port_code = $('#portCodeInp').val() ? $('#portCodeInp').val() : 0;
      const lodgement_type_id = $('#lodgementTypeIdInp').val() ? $('#lodgementTypeIdInp').val() : 0;
      const boc_status_id = $('#bocStatusIdInp').val() ? $('#bocStatusIdInp').val() : 0;
      const release_type_id = $('#releasedTypeIdInp').val() ? $('#releasedTypeIdInp').val() : 0;
      const commodity_type_id = $('#commodityTypeIdInp').val() ? $('#commodityTypeIdInp').val() : 0;
      if ($("select#statusId").length > 0) {
        const status_id = $('#statusIdInp').val() ? $('#statusIdInp').val() : 0;
        $('#statusId').val(status_id).trigger('change');
      }

      if ($("select#olrsStatusId").length > 0) {
        const status_id = $('#olrsStatusIdInp').val() ? $('#olrsStatusIdInp').val() : 0;
        $('#olrsStatusId').val(status_id).trigger('change');
      }

      if ($("select#manifestStatusId").length > 0) {
        const status_id = $('#manifestStatusIdInp').val() ? $('#manifestStatusIdInp').val() : 0;
        $('#manifestStatusId').val(status_id).trigger('change');
      }

      if ($("select#consigneeId").length > 0) {
        const consignee_id = $('#consigneeIdInp').val() ? $('#consigneeIdInp').val() : 0;
        $('#consigneeId').val(consignee_id).trigger('change');
      }

      if ($("select#exporterId").length > 0) {
        const exporter_id = $('#exporterIdInp').val() ? $('#exporterIdInp').val() : 0;
        $('#exporterId').val(exporter_id).trigger('change');
      }

      $('#companyCode').val(company_code_id).trigger('change');
      $('#importerId').val(importer_id).trigger('change');
      $('#brokerId').val(broker_id).trigger('change');
      $('#portCode').val(port_code).trigger('change');
      $('#lodgementTypeId').val(lodgement_type_id).trigger('change');
      $('#bocStatusId').val(boc_status_id).trigger('change');
      $('#releasedTypeId').val(release_type_id).trigger('change');
      $('#releasedTypeId').val(release_type_id).trigger('change');
      $('#commodityTypeId').val(commodity_type_id).trigger('change');
    }

    /**
     * Show mini loader in selections 
     */
    function showLoaderInSelections()
    {
      $('select[name!=form_searchTbl_length]:not(.skip-dropdown-reset)').prop("disabled", true);
      showElementLoader($('select[name!=form_searchTbl_length]:not(.skip-dropdown-reset)').parent());
    }

    /**
     * Hide min loader in selectiions
     */
    function hideLoaderInSelections()
    {
      $('select[name!=form_searchTbl_length]:not(.skip-dropdown-reset)').prop("disabled", false);
      hideElementLoader($('select[name!=form_searchTbl_length]:not(.skip-dropdown-reset)').parent());
    }

    $('#form_searchTbl').DataTable({
      drawCallback: function () {
        $('.dataTables_paginate > .pagination').addClass('pagination-rounded');
      },
      columnDefs: {
        className: "d-none",
        targets: [0]
      },
      order: [[0, 'desc']],
    });
  }

  if ($('[data-toggle="select2"]').length > 0) {
    $('[data-toggle="select2"]').select2();
    $('.modal select').select2({
      dropdownParent: $('.modal')
    });
  }

  if ($('[data-toggle="input-mask"]').length > 0) {
    $('[data-toggle="input-mask"]').each(function (a, e) {
      var t = $(e).data("maskFormat"), n = $(e).data("reverse");
      null != n ? $(e).mask(t, {reverse: n, placeholder: "-"}) : $(e).mask(t);
    });
  }

  $('[data-case="upper"]').each(function (a, e) {
    $(e).on('keyup', function () {
      $(this).val($(this).val().toUpperCase());
    });
  });
  //=================== START Custom validator ===================//

  /**
   * START additional methods
   * @see https://ajax.microsoft.com/ajax/jquery.validate/1.7/additional-methods.js
   */

  $.validator.addMethod("countryNotEqualTo", function (value, element, param) {
    if (param == "departure") {
      param = $('select[name=country_origin_id]').val();
    } else {
      param = $('select[name=country_destination_id]').val();
      console.log(param);
    }
    return this.optional(element) || $.trim(value) != param;
  }, "Please select a different departure or destination");
  $.validator.addMethod("airportNotEqualTo", function (value, element, param) {
    if (param == "departure") {
      param = $('select[name=departure_id]').val();
    } else {
      param = $('select[name=destination_id]').val();
    }
    return this.optional(element) || $.trim(value) != param;
  }, "Please select a different origin or destination");
  $.validator.addMethod("portNotEqualTo", function (value, element, param) {
    if (param == "departure") {
      param = $('select[name=departure_code]').val();
    } else {
      param = $('select[name=destination_code]').val();
    }
    return this.optional(element) || $.trim(value) != param;
  }, "Please select a different origin or destination");
  $.validator.addMethod("portofLoadingNotEqualTo", function (value, element, param) {
    if (param == "port_loading") {
      param = $('select[name=port_loading]').val();
    } else {
      param = $('select[name=port_departure]').val();
    }
    return this.optional(element) || $.trim(value) != param;
  }, "Please select a different Port of Loading or Port of Departure");
  $.validator.addMethod("portdestinationNotEqualTo", function (value, element, param) {
    if (param == "port_destination") {
      param = $('select[name=port_destination]').val();
    } else {
      param = $('select[name=transhipment_port]').val();
    }
    return this.optional(element) || $.trim(value) != param;
  }, "Value should not be the same as Transhipment Port");
  $.validator.addMethod("noValue", function (value, element, param) {
    return this.optional(element) || parseInt(value) > 0;
  }, "Please specify a different (non-default) value");
  $.validator.addMethod("notEqualTo", function (value, element, param) {
    return this.optional(element) || $.trim(value) != param;
  }, "Please specify a different (non-default) value");
  $.validator.addMethod("notGreaterThanTo", function (value, element, param) {
    return this.optional(element) || parseInt(param) >= parseInt($.trim(value));
  }, "Value should not be greater than the number of accepted pieces.");
  $.validator.addMethod("validText", function (value, element, param) {
    return this.optional(element) || /^(?!<script[^>]*>(.*?)<\/script>|<applet[^>]*>(.*?)<\/applet>|<embed[^>]*>(.*?)<\/embed>|<object[^>]*>(.*?)<\/object>)/.test($.trim(value).toLowerCase());
  }, "Please enter a valid value.");
  $.validator.addMethod("alphanumeric", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9]+$/i.test($.trim(value));
  }, "This field cannot contain spaces and special characters");
  $.validator.addMethod("alphanumericwithbasicpunc", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9-._]+$/i.test($.trim(value));
  }, "This field cannot contain spaces and special characters other than '.' '-' '_'");
  $.validator.addMethod("alphanumericWithSpaceOrBasicPunc", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9-._\s]+$/i.test($.trim(value));
  }, "This field cannot contain special characters other than '.' '-' '_'");
  $.validator.addMethod("validAddress", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9-.,_\/\s]+$/i.test($.trim(value));
  }, "This field cannot contain special characters other than '.' ',' '-' '_'");
  $.validator.addMethod("lettersonly", function (value, element) {
    return this.optional(element) || /^[a-zA-Z\s]*$/i.test($.trim(value));
  }, "Please enter a valid name");
  $.validator.addMethod("alphanumericAndDashOnly", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9-]*$/i.test($.trim(value));
  }, "This field cannot contain special characters other than '-'");
  $.validator.addMethod("numbersAndDashOnly", function (value, element) {
    return this.optional(element) || /^[0-9-]*$/i.test($.trim(value));
  }, "This field cannot contain special characters other than '-'");
  $.validator.methods.checkEntry = function (value, element) {
    return this.optional(element) || /([a-zA-Z]{3})+[0-9]{5,5}\-[0-9]{2,2}$/.test($.trim(value));
  }
  $.validator.methods.validMobileNumber = function (value, element) {
    return this.optional(element) || /^63\-+[0-9]{9,10}$/.test(value);
  }
  $.validator.addMethod("checkCity", function (value, element) {
    return this.optional(element) || /^[a-zA-Z0-9 .,]*$/i.test($.trim(value));
  }, "Please enter valid city address.");
  $.validator.addMethod("alphaWithSpaceOrBasicPunc", function (value, element) {
    return this.optional(element) || /^[a-zA-Z-._\s]+$/i.test($.trim(value));
  }, "This field cannot contain special characters other than '.' '-' '_'");

  //=================== END Custom validator ===================//

  $(".number-dash-only").on('keyup', function (e) {
    var val = $(this).val();
    if (val.match(/[^0-9-]/g)) {
      $(this).val(val.replace(/[^0-9-]/g, ''));
    }
  });

  $(".alphanumeric-dash-only").on('keyup', function (e) {
    var val = $(this).val();
    if (val.match(/[^a-zA-Z0-9-]/g)) {
      $(this).val(val.replace(/[^a-zA-Z0-9-]/g, ''));
    }
  }
  );
});

$.ajaxSetup({cache: false}); // prevents browser caching of AJAX call result

var objectToArray = obj => {
  const keys = Object.keys(obj);
  const res = [];
  for (let i = 0; i < keys.length; i++) {
    res.push(obj[keys[i]]);
  }
  ;
  return res;
};

/**
 * Show loader
 */
function showLoader()
{
  $("#preloader").show();
  $("#status").show();
}

/**
 * 
 * @param {*} e 
 */
function showElementLoader(e)
{
  if ($(e).find(".spinner-border").length == 0) { // Prevent from adding multiple spinner
    $(e).addClass("position-relative");
    $(e).append('<div class="spinner-border spinner-border-sm spinner-custom text-success m-2" role="status"></div>');
  }
}

/**
 * Hide Loader
 */
function hideLoader()
{
  $("#status").fadeOut(), $("#preloader").delay(350).fadeOut("slow");
}

/**
 * 
 * @param {*} e 
 */
function hideElementLoader(e)
{
  $(e).removeClass("position-relative");
  $(e).find('.spinner-border').remove();
}

/**
 * Display success message
 * @param {string} msg 
 */
function showSuccessMsg(msg, title = null, callback = null, cancelled_callback = null) {
  window.swalWithBootstrapButtons.fire({
    title: title ? title : "Great!",
    text: msg ? msg : "",
    icon: 'success',
    confirmButtonText: 'OK'
  }).then((result) => {
    if (result.isConfirmed) {
      if (callback != null) {
        callback();
      }
    } else {
      hideLoader();

      if (cancelled_callback != null) {
        cancelled_callback();
      }
    }
  });
}

/**
 * 
 * @param {*} msg 
 */
function showWarningMsg(msg, title = null, callback = null, cancelled_callback = null, confirm_button_text = "Yes", cancel_button_text = "No") {
  window.swalWithBootstrapButtons.fire({
    title: title ? title : 'Confirmation',
    html: msg,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: confirm_button_text,
    cancelButtonText: cancel_button_text
  }).then((result) => {
    if (result.isConfirmed) {
      if (callback != null) {
        callback();
      }
    } else {
      hideLoader();

      if (cancelled_callback != null) {
        cancelled_callback();
      }
    }
  });
}

/**
 * Display error message
 * @param {string} msg 
 */
function showErrorMsg(msg = null, title = null, callback) {
  window.swalWithBootstrapButtons.fire({
    title: title ? title : "Oops!",
    html: msg ? msg : "There was an error while processing your request. Please try again.",
    icon: 'error',
    confirmButtonText: 'OK'
  }).then((result) => {
    if (callback) {
      callback();
    }
  });
}

/**
 * Reset fields
 * @param {element} form 
 */
function resetForm(form, reset_all = false) {
  if (!reset_all) {
    form.find('input, textarea').not(':input[name=id]').not('.skip-reset').val("");
  } else {
    form.find('input, textarea').not('.skip-reset').val("");
  }

  form.find('select').val(0).trigger('change');
  form.find('select.select2').val('0').trigger('change');
  form.find('input:checkbox').prop('checked', false);

  removeValidationClass(form);
}

/**
 * 
 * @param {*} form 
 */
function removeValidationClass(form) {
  form.find('input, textarea, select').removeClass('is-invalid error text-danger'); // remove classes for validation
  form.find('input, textarea, select').removeClass('is-valid');
}

/**
 * 
 * @param {*} form 
 * @returns 
 */
function extractFormData(form) {
  return Object.values(form).reduce((obj, field) => {
    if (field.name) {
      obj[field.name] = field.value;
    }

    return obj
  }, {})
}

/**
 * 
 */
function disableSaveButtons() {
  $.each($('button[type=submit]'), function () {
    if (!$(this).prop('disabled')) {
      console.log('disable buttons');
      $("button[type=submit]").prop("disabled", true);
    }
  });
}

/**
 * 
 */
function enableSaveButtons() {
  $.each($('button[type=submit]'), function () {
    if ($(this).prop('disabled')) {
      console.log('enable buttons');
      $("button[type=submit]").prop("disabled", false);
    }
  });
}

/**
 * 
 */
function disableModalButtons() {
  $("button").prop("disabled", true);
}

/**
 * 
 */
function enableModalButtons() {
  $("button").prop("disabled", false);
}




//added mobile mask and telephone mask
function initMobileMask(el = null) {
  if ($(".mobile-number-mask").length > 0) {
    if (el) {
      el.unmask();
      el.mask('+63-0000000000', {
        translation: {
          '0': {pattern: /[0-9]/}
        },
        placeholder: "+63-0000000000"
      });
    } else {
      $(".mobile-number-mask").mask('+63-0000000000', {
        translation: {
          '0': {pattern: /[0-9]/}
        },
        placeholder: "+63-0000000000"
      });

      $(".mobile-number-mask").focus(function () {
        var prefix = "+63-"

        if (this.value.indexOf(prefix) !== 0 && this.value == "") {
          this.value = prefix + this.value;
        }
      });
    }
}
}

function initTelephoneMask(el = null) {
  if ($(".telephone-number-mask").length > 0) {
    if (el) {
      el.unmask();
      el.mask('+63Y-00000000', {
        translation: {
          'Y': {pattern: /[0-9]/},
          '0': {pattern: /^[0-9]/}
        },
        placeholder: "+632-80000000"
      });
    } else {
      $(".telephone-number-mask").mask('+63Y-00000000', {
        translation: {
          'Y': {pattern: /[0-9]/},
          '0': {pattern: /^[0-9]/}
        },
        placeholder: "+632-00000000"
      });

      $(".telephone-number-mask").focus(function () {
        var prefix = "+632-"

        if (this.value.indexOf(prefix) !== 0) {
          this.value = prefix + this.value;
        }
      });
    }
}
}


// if (form_exportDeclaration.valid()) {
//     form_exportDeclaration.find("button[type=submit]")
//         .prop("disabled", true);

//     if( action == 2 ){
//         form_exportDeclaration.find("button[id=btnSaveAndAddItem]").html(btn_loader_html);
//     }else {
//         form_exportDeclaration.find("button[id=btnSave]").html(btn_loader_html);
//     }
// }else{
//     form_exportDeclaration.find("button[type=submit]").prop("disabled", false);
//     form_exportDeclaration.find("button[id=btnSave]").html('Save');
//     form_exportDeclaration.find("button[id=btnSaveAndAddItem]").html('Save & Add New');
// }
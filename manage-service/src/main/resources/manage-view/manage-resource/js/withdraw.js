$(function () {
    var startTime = $("#J_startPicker").flatpickr({
        maxDate: new Date().fp_incr(-1),
        locale: 'zh',
        onChange: function(selectedDates, dateStr, instance) {
            endTime.set("minDate", new Date(dateStr).fp_incr(1));
        }
    });
    var endTime = $("#J_endPicker").flatpickr({
        maxDate: new Date(),
        locale: 'zh',
        onChange: function(selectedDates, dateStr, instance) {
            startTime.set("maxDate", new Date(dateStr).fp_incr(-1));
        }
    });

    var table = $('#withdrawTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').data('url'),
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": true,
        "lengthChange": false,
        "searching": false,
        "colReorder": true,
        "columns": [
            {
                "title": "流水号", "data": "serial", "name": "serial"
            },
            {
                "title": "代理商", "data": "dealer", "name": "dealer"
            },
            {
                "title": "提现金额（元）", "data": "withdrawAmount", "name": "withdrawAmount"
            },
            {
                "title": "剩余可提现金额（元）", "data": "withdrawBalance", "name": "withdrawBalance"
            },
            {
                "title": "提现区间", "name": "divided", "orderable": false,
                data: function (item) {
                    return item.startDate + ' ~ ' + item.endDate;
                }
            },
            {
                "title": "收款帐号", "data": "account", "name": "account"
            },
            {
                "title": "收款人", "data": "payee", "name": "payee"
            },
            {
                "title": "发票物流", "name": "commTime","orderable": false,
                data: function (item) {
                    return item.logisticsCompany + ' ' + item.logisticsId;
                }
            },
            {
                "title": "提现时间", "data": "withdrawTime", "name": "withdrawTime"
            },
            {
                "title": "状态", "data": "status", "name": "status"
            },
            {
                "title": "审核人", "data": "operator", "name": "operator"
            },
            {
                "title": "备注", "data": "remark", "name": "remark"
            },
            {
                "title": "操作",
                "className": 'table-action',
                "orderable": false,
                data: function (item) {
                    var a = '<a href="javascript:;" class="js-info" data-id="' + item.id + '"><i class="fa fa-check-circle-o"></i>&nbsp;查看</a>';
                    if(+item.statusCode === 1)
                        a += '<a href="javascript:;" class="btn btn-primary btn-xs js-pending" data-id="' + item.id + '">审核</a>';
                    return a;
                }
            }
        ],
        "displayLength": 15,
        "drawCallback": function () {
            // clearSearchValue();
        },
        "dom": "<'row'<'col-sm-12'B>>" +
        "<'row'<'col-sm-12'tr>>" +
        "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "buttons": [{
            "extend": "excel",
            "text": "导出 Excel",
            "className": "btn-xs",
            "exportOptions": {
                "columns": ":not(.table-action)"
            }
        },{
            "extend": 'colvis',
            "text": "筛选列",
            "className": "btn-xs"
        }]
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    }).on('click', '.js-info', function () {
        window.location.href = '_withdrawDetail.html?id=' + $(this).data('id');
    }).on('click', '.js-pending', function () {
        window.location.href = '_withdrawPending.html?id=' + $(this).data('id');
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        table.ajax.reload();
    });

    // 添加额外的参数
    function extendData() {
        var formItem = $('.js-selectToolbar').find('.form-control');
        if (formItem.length === 0)  return {};
        var data = {};

        formItem.each(function () {
            var t = $(this);
            var n = t.attr('name');
            var v = t.val();
            if (v) data[n] = v;
        });
        // 获取当前tab
        data['status'] = $('.js-orderStatus').find('.active').find('a').data('status');
        return data;
    }

});
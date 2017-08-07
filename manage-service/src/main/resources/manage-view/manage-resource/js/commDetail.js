$(function () {
    var table = $('#commTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": $('body').attr('data-comm-url'),
            "data": function (d) {
                return $.extend({}, d, extendData());
            }
        },
        "ordering": false,
        "lengthChange": false,
        "searching": false,
        "columns": [
            {
                title: '',
                target: 0,
                className: 'treegrid-control table-action',
                data: function (item) {
                    if (item.children) {
                        return '<span><i class="fa fa-chevron-right"></i></span>';
                    }
                    return '';
                }
            },
            {
                "title": "代理商名称", "data": "dealer", "name": "dealer"
            },
            {
                "title": "代理商级别", "data": "rank", "name": "rank"
            },
            {
                "title": "佣金类型", "data": "commType", "name": "commType"
            },
            {
                "title": "订单编号", "data": "orderId", "name": "orderId"
            },
            {
                "title": "订单总额（元）", "data": "orderTotal", "name": "orderTotal"
            },
            {
                "title": "分成比例", "data": "proportion", "name": "proportion"
            },
            {
                "title": "佣金（元）", "data": "commission", "name": "commission"
            },
            {
                "title": "获佣时间", "data": "commTime", "name": "commTime"
            }
        ],
        "treeGrid": {
            "left": 20,
            "expandIcon": '<span><i class="fa fa-chevron-right"></i></span>',
            "collapseIcon": '<span><i class="fa fa-chevron-down"></i></span>'
        },
        "displayLength": 15,
        "drawCallback": function () {
            clearSearchValue();
        }
    });

    $(document).on('click', '.js-search', function () {
        table.ajax.reload();
    });

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
        return data;
    }

    function clearSearchValue() {
        //TODO
    }
});
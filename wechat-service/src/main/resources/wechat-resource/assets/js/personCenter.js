/**
 * Created by Neo on 2017/5/10.
 */
$(function () {

    var tabsItem = $('.js-comm');
    var tabsSwiper = $('#tabs-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".js-comms .active").removeClass('active');
            tabsItem.eq(tabsSwiper.activeIndex).addClass('active');
        }
    });
    tabsItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".js-comms .active").removeClass('active');
        $(this).addClass('active');
        tabsSwiper.slideTo($(this).index())
    });
    tabsItem.click(function (e) {
        e.preventDefault();
    });

    var teamItem = $('.js-team');
    var teamSwiper = $('#tabs-teams').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".js-teams .active").removeClass('active');
            teamItem.eq(teamSwiper.activeIndex).addClass('active');

            $('.view-total .on').removeClass('on');
            $('.view-total li').eq(teamSwiper.activeIndex).addClass('on');
        }
    });
    teamItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".js-teams .active").removeClass('active');
        $(this).addClass('active');
        teamSwiper.slideTo($(this).index())
    });
    teamItem.click(function (e) {
        e.preventDefault();
    });

    var navbarItem = $('.view-navbar_item');
    var navbarSwiper = $('#navbar-container').swiper({
        observer: true,
        observeParents: true,
        speed: 500,
        onSlideChangeStart: function () {
            $(".weui-navbar .on").removeClass('on');
            navbarItem.eq(navbarSwiper.activeIndex).addClass('on');
        }
    });
    navbarItem.on('touchstart mousedown', function (e) {
        e.preventDefault();
        $(".weui-navbar .on").removeClass('on');
        $(this).addClass('on');
        navbarSwiper.slideTo($(this).index())
    });
    navbarItem.click(function (e) {
        e.preventDefault();
    });

    var extraHeight = 0;
    var commItems = $('.js-commItems');
    $('.js-extra-h-c').each(function () {
        extraHeight += $(this).outerHeight(true);
    });
    commItems.height($(window).height() - Math.ceil(extraHeight) - 52);

    var commTpl = function (obj) {
        return '<div class="view-comm-list_item"> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">' + obj.commType + '</div> ' +
            '<div class="weui-flex__item">' + obj.name + '</div> ' +
            '<div class="weui-flex__item"><strong>￥' + obj.commission + '</strong></div> ' +
            '</div> ' +
            '<div class="weui-flex"> ' +
            '<div class="weui-flex__item">(' + obj.divided + '分成）</div> ' +
            '<div class="weui-flex__item">' + obj.commInfo + '</div> ' +
            '<div class="weui-flex__item text-gray">' + obj.commTime + '</div> ' +
            '</div> ' +
            '</div>';
    };

    commItems.each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: commTpl
        });
    });


    // 我的团队逻辑
    var teamItems = $('.js-teamItems');

    var extraHeight_team = 0;
    $('.js-extra-h').each(function () {
        extraHeight_team += $(this).outerHeight(true);
    });

    teamItems.height($(window).height() - Math.ceil(extraHeight_team) -52);

    var listTpl = function (obj) {
        return '<div class="weui-flex view_team-list">' +
            '<div class="weui-flex__item text-center">' + obj.name + '</div>' +
            '<div class="weui-flex__item text-center">' + obj.rank + '</div>' +
            '<div class="weui-flex__item text-center">' + obj.joinTime + '</div>' +
            '</div>';
    };

    teamItems.each(function () {
        var self = $(this);
        self.myScroll({
            ajaxUrl: self.attr('data-url'),
            template: listTpl
        });
    })

});
/**
 * 右键菜单模块
 * date:2019-04-23   License By http://easyweb.vip
 */
layui.define(["jquery"], function (exports) {
    var $ = layui.jquery;

    var contextMenu = {
        // 绑定元素
        bind: function (elem, items) {
            $(elem).bind('contextmenu', function (e) {
                contextMenu.show(items, e.clientX, e.clientY);
                return false;
            });
        },
        // 构建无限级
        getHtml: function (items, pid) {
            var htmlStr = '';
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                item.itemId = 'ctxMenu-' + pid + i;
                if (item.subs && item.subs.length > 0) {
                    htmlStr += '<div class="ctxMenu-item haveMore" lay-id="' + item.itemId + '">';
                    htmlStr += '<a>';
                    if (item.icon) {
                        htmlStr += '<i class="' + item.icon + ' ctx-icon"></i>';
                    }
                    htmlStr += item.name;
                    htmlStr += '<i class="layui-icon layui-icon-right icon-more"></i>';
                    htmlStr += '</a>';
                    htmlStr += '<div class="ctxMenu-sub" style="display: none;">';
                    htmlStr += contextMenu.getHtml(item.subs, pid + i);
                    htmlStr += '</div>';
                } else {
                    htmlStr += '<div class="ctxMenu-item" lay-id="' + item.itemId + '">';
                    htmlStr += '<a>';
                    if (item.icon) {
                        htmlStr += '<i class="' + item.icon + ' ctx-icon"></i>';
                    }
                    htmlStr += item.name;
                    htmlStr += '</a>';
                }
                htmlStr += '</div>';
                if (item.hr == true) {
                    htmlStr += '<hr/>';
                }
            }
            return htmlStr;
        },
        // 设置事件监听
        setEvents: function (items) {
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                if (item.click) {
                    $('.ctxMenu').on('click', '[lay-id="' + item.itemId + '"]', item.click);
                }
                if (item.subs && item.subs.length > 0) {
                    contextMenu.setEvents(item.subs);
                }
            }
        },
        // 移除所有
        remove: function () {
            var ifs = top.window.frames;
            for (var i = 0; i < ifs.length; i++) {
                var tif = ifs[i];
                try {
                    tif.layui.jquery('body>.ctxMenu').remove();
                } catch (e) {
                }
            }
            try {
                top.layui.jquery('body>.ctxMenu').remove();
            } catch (e) {
            }
        },
        // 获取浏览器高度
        getPageHeight: function () {
            return document.documentElement.clientHeight || document.body.clientHeight;
        },
        // 获取浏览器宽度
        getPageWidth: function () {
            return document.documentElement.clientWidth || document.body.clientWidth;
        },
        // 在指定坐标显示菜单
        show: function (items, x, y) {
            var xy = 'left: ' + x + 'px; top: ' + y + 'px;';
            var htmlStr = '<div class="ctxMenu" style="' + xy + '">';
            htmlStr += contextMenu.getHtml(items, '');
            htmlStr += '   </div>';
            contextMenu.remove();
            $('body').append(htmlStr);
            // 调整溢出位置
            var $ctxMenu = $('.ctxMenu');
            if (x + $ctxMenu.outerWidth() > contextMenu.getPageWidth()) {
                x -= $ctxMenu.outerWidth();
            }
            if (y + $ctxMenu.outerHeight() > contextMenu.getPageHeight()) {
                y = y - $ctxMenu.outerHeight();
                if (y < 0) {
                    y = 0;
                }
            }
            $ctxMenu.css({'top': y, 'left': x});
            // 添加item点击事件
            contextMenu.setEvents(items);
            // 显示子菜单事件
            $('.ctxMenu-item.haveMore').on('mouseenter', function () {
                var $item = $(this).find('>a');
                var $sub = $(this).find('>.ctxMenu-sub');
                var top = $item.offset().top;
                var left = $item.offset().left + $item.outerWidth();
                if (left + $sub.outerWidth() > contextMenu.getPageWidth()) {
                    left = $item.offset().left - $sub.outerWidth();
                }
                if (top + $sub.outerHeight() > contextMenu.getPageHeight()) {
                    top = top - $sub.outerHeight() + $item.outerHeight();
                    if (top < 0) {
                        top = 0;
                    }
                }
                $(this).find('>.ctxMenu-sub').css({
                    'top': top,
                    'left': left,
                    'display': 'block'
                });
            }).on('mouseleave', function () {
                $(this).find('>.ctxMenu-sub').css('display', 'none');
            });
        },
        // 获取css代码
        getCommonCss: function () {
            var cssStr = '.ctxMenu, .ctxMenu-sub {';
            cssStr += '        max-width: 250px;';
            cssStr += '        min-width: 110px;';
            cssStr += '        background: white;';
            cssStr += '        border-radius: 2px;';
            cssStr += '        padding: 5px 0;';
            cssStr += '        white-space: nowrap;';
            cssStr += '        position: fixed;';
            cssStr += '        z-index: 2147483647;';
            cssStr += '        box-shadow: 0 2px 4px rgba(0, 0, 0, .12);';
            cssStr += '        border: 1px solid #d2d2d2;';
            cssStr += '        overflow: visible;';
            cssStr += '   }';

            cssStr += '   .ctxMenu-item {';
            cssStr += '        position: relative;';
            cssStr += '   }';

            cssStr += '   .ctxMenu-item > a {';
            cssStr += '        font-size: 14px;';
            cssStr += '        color: #666;';
            cssStr += '        padding: 0 26px 0 35px;';
            cssStr += '        cursor: pointer;';
            cssStr += '        display: block;';
            cssStr += '        line-height: 36px;';
            cssStr += '        text-decoration: none;';
            cssStr += '        position: relative;';
            cssStr += '   }';

            cssStr += '   .ctxMenu-item > a:hover {';
            cssStr += '        background: #f2f2f2;';
            cssStr += '        color: #666;';
            cssStr += '   }';

            cssStr += '   .ctxMenu-item > a > .icon-more {';
            cssStr += '        position: absolute;';
            cssStr += '        right: 5px;';
            cssStr += '        top: 0;';
            cssStr += '        font-size: 12px;';
            cssStr += '        color: #666;';
            cssStr += '   }';

            cssStr += '   .ctxMenu-item > a > .ctx-icon {';
            cssStr += '        position: absolute;';
            cssStr += '        left: 12px;';
            cssStr += '        top: 0;';
            cssStr += '        font-size: 15px;';
            cssStr += '        color: #666;';
            cssStr += '   }';

            cssStr += '   .ctxMenu hr {';
            cssStr += '        background-color: #e6e6e6;';
            cssStr += '        clear: both;';
            cssStr += '        margin: 5px 0;';
            cssStr += '        border: 0;';
            cssStr += '        height: 1px;';
            cssStr += '   }';

            cssStr += '   .ctx-ic-lg {';
            cssStr += '        font-size: 18px !important;';
            cssStr += '        left: 11px !important;';
            cssStr += '    }';
            return cssStr;
        }
    };

    // 点击任意位置关闭菜单
    $(document).off('click.ctxMenu').on('click.ctxMenu', function () {
        contextMenu.remove();
    });

    // 点击有子菜单的节点不关闭菜单
    $(document).off('click.ctxMenuMore').on('click.ctxMenuMore', '.ctxMenu-item', function (e) {
        if ($(this).hasClass('haveMore')) {
            if (e !== void 0) {
                e.preventDefault();
                e.stopPropagation();
            }
        } else {
            contextMenu.remove();
        }
    });

    $('head').append('<style>' + contextMenu.getCommonCss() + '</style>');
    exports("contextMenu", contextMenu);
});
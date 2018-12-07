/**
 * Created by billJiang on 2017/6/19.
 * 组织机构选择器
 */
(function ($, window, document, undefined) {
    'use strict';

    var pluginName = 'org';

    $.fn[pluginName] = function (options) {
        var self = $(this);
        if (this == null)
            return null;
        var data = this.data(pluginName);
        if (!data) {
            data = new BaseOrg(this, $.extend(true, {}, options));
            self.data(pluginName, data);
        }
    }

    var BaseOrg = function (element, options) {
        this.element = element;
        this.options = $.extend(true, {}, this.default, options);
        this.data = this.getOrgData();
        this.orgId = this.options.orgId || "orgWin";
        var self = this;
        $(this.element).unbind("click");
        $(this.element).click(function () {
            modals.openWin({
                winId: 'orgWin',
                width: '300px',
                url: false,
                loadContent: $.proxy(self.loadContent, self)
            });
        })


    };

    //加载组织结构内容结构
    BaseOrg.prototype.loadContent = function () {
        this.treeId = this.orgId + "_tree";
        //header 和 content
        $("#" + this.orgId).find("div.modal-content").append('<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><li class="fa fa-remove"></li></button>' +
            '<h5 class="modal-title"><i class="fa fa-edit"></i>&nbsp;' + this.options.title + '</h5></div><div class="modal-body"><div id="' + this.treeId + '"></div></div>');
        if (this.options.minHeight)
            $("#" + this.orgId).find("div.modal-body").css("min-height", this.options.minHeight + "px");
        $("#" + this.orgId).find("div.modal-header").css("padding", "10px");
        //footer
        $("#" + this.orgId).find("div.modal-content").append('<div class="modal-footer"></div>');
        $("#" + this.orgId).find("div.modal-footer").append('<button class="btn btn-primary" type="button" data-btn-type="selectOrg">确定</button>')
        $("#" + this.orgId).find("div.modal-footer").append('<button class="btn btn-primary" type="button" data-btn-type="cancelOrg">取消</button>')
        $("#" + this.orgId).find("div.modal-footer").append('<button class="btn btn-primary" type="button" data-btn-type="clearOrg">清除</button>')
        this.initOrgTree();
        var self = this;
        $("button[data-btn-type='selectOrg']").click(function () {
            self.selectOrg();
        })

        $("button[data-btn-type='cancelOrg']").click(function () {
            self.cancelOrg();
        })

        $("button[data-btn-type='clearOrg']").click(function () {
            self.clearOrg();
        })
    }


    //加载内容
    BaseOrg.prototype.initOrgTree = function () {
        var self = this;
        $("#" + this.treeId).treeview({
            data: self.data,
            showBorder: true,
            levels: self.options.levels,
            multiSelect: false,
            showIcon: false
        })
        //回填选中
        var selectId = this.options.idField.val();
        var selectedNode = $("#" + this.treeId).data("treeview").getSelected();
        if (!selectId) {
            if (selectedNode.length > 0) {
                for (var i = 0; i < selectedNode.length; i++)
                    $("#" + this.treeId).data("treeview").unselectNode(selectedNode[i]);
            }
        } else {
            if (selectedNode.length > 0) {
                for (var i = 0; i < selectedNode.length; i++)
                    $("#" + this.treeId).data("treeview").unselectNode(selectedNode[i]);
            }
            var nodes = $("#" + this.treeId).data("treeview").getUnselected();
            for (var i = 0; i < nodes.length; i++) {
                if (nodes[i].id == selectId) {
                    $("#" + this.treeId).data("treeview").selectNode(nodes[i]);
                    break;
                }
            }
        }
    }

    //确定选择组织机构
    BaseOrg.prototype.selectOrg = function () {
        var selectedNode = $("#" + this.treeId).data("treeview").getSelected();
        if (selectedNode.length > 0) {
            if (this.options.idField)
                this.options.idField.val(selectedNode[0].id);
            if (this.options.nameField)
                this.options.nameField.val(selectedNode[0].text);
        } else {
            if (this.options.idField)
                this.options.idField.val("");
            if (this.options.nameField)
                this.options.nameField.val("");
        }
        modals.hideWin(this.orgId);

    }

    //清除后关闭
    BaseOrg.prototype.clearOrg = function () {
        var selectedNode = $("#" + this.treeId).data("treeview").getSelected();
        for (var i = 0; i < selectedNode.length; i++)
            $("#" + this.treeId).data("treeview").unselectNode(selectedNode[i]);
        if (this.options.idField)
            this.options.idField.val("");
        if (this.options.nameField)
            this.options.nameField.val("");
        modals.hideWin(this.orgId);
    }
    //取消，关闭窗口
    BaseOrg.prototype.cancelOrg = function () {
        modals.hideWin(this.orgId);
    }


    //组织机构选择器默认配置
    BaseOrg.prototype.default = {
        orgId: "orgWin",
        title: '组织机构选择器',
        levels: 2,
        minHeight: 300
    }

    BaseOrg.prototype.getOrgData = function (orgType) {
        var treeData;
        ajaxPost(basePath + "/org/treeData", null, function (data) {
            treeData = data;
        });
        return treeData;
    }



})(jQuery, window, document)
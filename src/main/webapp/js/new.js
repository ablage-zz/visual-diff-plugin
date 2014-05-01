(function () {

    var templates = {};

    Handlebars.registerHelper('compare', function (left, condition, right, options) {
        var result = false;

        switch (condition) {
            case '==':
                result = (left == right);
                break;
            case '!=':
                result = (left != right);
                break;
            case '===':
                result = (left === right);
                break;

            case '<':
                result = (left < right);
                break;
            case '<=':
                result = (left <= right);
                break;

            case '>':
                result = (left > right);
                break;
            case '>=':
                result = (left >= right);
                break;
        }

        if (!result) {
            return options.inverse(this);
        } else {
            return options.fn(this);
        }
    });

    function getData () {
        return screenData;
    }

    function loadTemplates () {
        templates.rowView = Handlebars.compile($('#row_view').html());
        templates.compareView = Handlebars.compile($('#compare_view').html());
    }
    loadTemplates();

    function prepareList () {
        var data = getData();

        _.each(data, function (entry, index) {
            entry.index = index;
        });

        if (data.length > 0) {
            data[0].first = true;
            data[data.length - 1].last = true;
        }
    }
    prepareList();

    function updateList (options) {
        var data = _.extend({ screenData: getData() }, options || {}),
            template = templates.rowView,
            table = $('#compare_table'),
            code = template(data);

        table.html(code);
        table.on('click', '.name a', _onRowClick);
    }

    function _onRowClick (e) {
        var $target = $(e.target),
            $row = $target.closest('tr'),
            index = parseInt($row.data('index'), 10);

        e.preventDefault();
        e.stopPropagation();

        showScreen(index);
    }

    function showNextScreen () {
        var index = getCurrentScreenIndex();
        index++;
        if (index == getData().length) index = 0;
        showScreen(index);
    }
    function showPreviousScreen () {
        var index = getCurrentScreenIndex();
        index--;
        if (index < 0) index = getData().length - 1;
        showScreen(index);
    }

    function getCurrentScreenIndex () {
        return parseInt($('#compare_overlay .overlay-view').data('index'), 10);
    }

    function showScreen (index, options) {
        var screenData = getData()[index],
            data = _.extend({}, screenData, options || {}),
            imageName = data.imageName,
            images;

        data.approvedUrl = "approvedScreens/" + imageName;
        data.buildUrl = "buildScreens/" + imageName;
        data.maskUrl = "buildDiffs/" + imageName;

        images = [ data.approvedUrl, data.buildUrl, data.maskUrl ];

        showLoading();

        loadImages(images, function () {
            var template = templates.compareView,
                code = template(data),
                overlay = $('#compare_overlay');

            hideLoading();

            overlay.html(code);

            overlay.on('click', 'a.link-next', function (e) {
                e.preventDefault();
                e.stopPropagation();

                showNextScreen();
            });
            overlay.on('click', 'a.link-prev', function (e) {
                e.preventDefault();
                e.stopPropagation();

                showPreviousScreen();
            });

            overlay.on('click', '.close-box', function () {
                hideScreen();
            });

            overlay.on('change', '.control-container input.difference-check', function (event) {
                var $target = $(event.target);
                overlay.find('.image-frame img.image-highlight').css('opacity', $target[0].checked ? 0.5 : 0.0001);
            });

            overlay.find(".control-container .compare-slider").slider({
                min: 0,
                max: 100,
                value: 50,
                slide: function (event, ui) {
                    var value = ui.value,
                        rightOpacity = value / 100,
                        leftOpacity = Math.abs(1 - rightOpacity);

                    overlay.find('.image-frame img.image-build').css('opacity', rightOpacity);
                    overlay.find('.image-frame img.image-approved').css('opacity', leftOpacity);
                }
            });

            overlay.show();
        });
    }
    function hideScreen () {
        $('#compare_overlay').hide();
    }

    function showLoading () {
        $('#compare_loading').show();
    }
    function hideLoading () {
        $('#compare_loading').hide();
    }

    function loadImages (images, callback) {

        var imageList = [];

        _.each(images, function (image) {
            var newImage = new Image();
            newImage.src = image;
            imageList.push(newImage);
        });

        checkImagesLoaded();
        function checkImagesLoaded() {

            var successful = true,
                resolution;

            _.each(imageList, function (imageEntry) {
                successful = successful && imageEntry.complete;
            });

            if (successful) {
                callback(imageList);
            } else {
                setTimeout(checkImagesLoaded, 100);
            }
        }
    }

    updateList();

    hideLoading();
    hideScreen();
}());
(function () {

    function getTitleBox(el) {
        var $target = $(el),
            $titleBox = $target.closest('.image-title-box');
        return $titleBox;
    }

    function getCompareId(el) {
        return getTitleBox(el).data('id');
    }

    $(document).ready(function () {
        $(".image-title").click(function (e) {
            var $titleBox = getTitleBox(e.target);

            $('.mask').show();

            var $imageCompareBox = $titleBox.find('.image-compare-box');
            var maxWidth = 0,
                maxHeight = 0,

                imgScreen = $imageCompareBox.find('img.screen'),
                imgApproved = $imageCompareBox.find('img.approved'),
                imgDiff = $imageCompareBox.find('img.diff');

            if (imgScreen.length > 0) {
                imgScreen[0].src = imgScreen[0].alt;
            }
            if (imgApproved.length > 0) {
                imgApproved[0].src = imgApproved[0].alt;
            }
            if (imgDiff.length > 0) {
                imgDiff[0].src = imgDiff[0].alt;
            }

            loadImages();
            function loadImages() {

                var successful = true,
                    $sliderBox;

                if (imgScreen.length > 0) {
                    if (!imgScreen[0].complete) {
                        successful = false;
                    }
                }
                if (imgApproved.length > 0) {
                    if (!imgApproved[0].complete) {
                        successful = false;
                    }
                }
                if (imgDiff.length > 0) {
                    if (!imgDiff[0].complete) {
                        successful = false;
                    }
                }

                if (successful) {

                    $sliderBox = $imageCompareBox.find('.slider-box');
                    $sliderBox.css('left', Math.max(0, ($(window).width() / 2) - ($sliderBox.outerWidth() / 2)))

                    $imageCompareBox.show();

                    maxWidth = Math.max(imgScreen.outerWidth(), imgApproved.outerWidth());
                    maxHeight = Math.max(imgScreen.outerHeight(), imgApproved.outerHeight());

                    if (imgDiff.length > 0) {
                        imgDiff.css('width', imgScreen.outerWidth() + 'px');
                        imgDiff.css('height', imgScreen.outerHeight() + 'px');
                    }

                    $imageCompareBox.css('width', maxWidth + 'px');
                    $imageCompareBox.css('height', ($(window).height() - 140 - 25) + 'px');

                    $imageCompareBox.css('top', Math.max(140, ($(window).height() / 2) - (maxHeight / 2) - (140/2)));
                    $imageCompareBox.css('left', Math.max(0, ($(window).width() / 2) - (maxWidth / 2)));

                } else {
                    setTimeout(loadImages, 500);
                }
            }
        });
        $(".image-compare-box .close-link").click(function (e) {
            var $titleBox = getTitleBox(e.target);

            $('.mask').hide();
            $titleBox.find('.image-compare-box').hide();
        });


        $( ".image-compare-box .slider-box .slider" ).slider({
            min: 0,
            max: 100,
            value: 50,
            slide: function (event, ui) {
                var $titleBox = getTitleBox(event.target),
                    value = ui.value,
                    rightOpacity = value/100,
                    leftOpacity = Math.abs(1 - rightOpacity);

                $titleBox.find('.image-compare-box img.screen').css('opacity', leftOpacity);
                $titleBox.find('.image-compare-box img.approved').css('opacity', rightOpacity);
            }
        });
        $(".image-compare-box .slider-box input").change(function (event) {
            var $target = $(event.target),
                $titleBox = getTitleBox(event.target);

            $titleBox.find('.image-compare-box img.diff').css('opacity', $target[0].checked ? 0.5 : 0.0001);
        });
        $(".image-compare-box .approve-link").click(function (event) {
            var $titleBox = getTitleBox(event.target),
                id = getCompareId(event.target);

            $.get('approve?name=' + id, '', function() {
                window.location.reload();
            });
        });
        $(".image-compare-box .delete-link").click(function (event) {
            var $titleBox = getTitleBox(event.target),
                id = getCompareId(event.target);

            if (confirm("Are you sure that you want to delete the approved screen?")) {
                $.get('delete?name=' + id, '', function() {
                    window.location.reload();
                });
            }
        });
        $(".delete-all-link").click(function (event) {
            if (confirm("Are you sure that you want to delete all approved screens?")) {
                $.get('deleteAll', '', function() {
                    window.location.reload();
                });
            }
        });
    });
})();
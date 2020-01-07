import os
import glob
import re
from distutils.version import StrictVersion
from io import BytesIO

from automation.core import logger
from automation.core.components.ui.browser import get_browser


def save_screenshot(reportdir, image_name, format='PNG', quality=None, width=None,
                    height=None, resize=None):
    """Modify screenshot format, size and quality before saving.
    Pillow must be installed.

    - format must be 'PNG' or 'JPEG'
    - quality must be an int in 1..95 range.
        Default is 75. Only applies to JPEG.
    - width and height must be int greater than 0
    - resize must be an int greater than 0.
        Str in the format '55' or '55%' is also allowed.
    """
    try:
        from PIL import Image
    except ModuleNotFoundError:
        logger.log_warning('Pillow must be installed in order to modify'
                                 ' screenshot format, size or quality')
        return

    extension = 'png'
    resample_filter = Image.BOX  # for PNG

    # validate format
    if format not in ['JPEG', 'PNG']:
        raise ValueError("settings screenshots format should be 'jpg' or 'png'")
    # validate quality
    if quality is not None:
        try:
            quality = int(quality)
        except ValueError:
            raise ValueError('settings screenshots quality should be int')
        if format == 'JPEG' and not 1 <= quality <= 95:
            raise ValueError('settings screenshots quality should be in 1..95 range for jpg files')
    # validate width
    if width is not None:
        try:
            width = int(width)
        except ValueError:
            raise ValueError('settings screenshots width should be int')
        if width < 0:
            raise ValueError('settings screenshots width should be greater than 0')
    # validate height
    if height is not None:
        try:
            height = int(height)
        except ValueError:
            raise ValueError('settings screenshots height should be int')
        if height < 0:
            raise ValueError('settings screenshots height should be greater than 0')
    # validate resize
    if resize is not None:
        if resize is str:
            resize = resize.replace('%', '')
        try:
            resize = int(resize)
        except ValueError:
            raise ValueError('settings screenshots resize should be int')
        if resize < 0:
            raise ValueError('settings screenshots resize should be greater than 0')

    base_png = get_browser().get_screenshot_as_png()
    pil_image = Image.open(BytesIO(base_png))

    if format == 'JPEG':
        pil_image = pil_image.convert('RGB')
        extension = 'jpg'
        resample_filter = Image.BICUBIC

    if any([width, height, resize]):
        img_width, img_height = pil_image.size
        if width and height:
            new_width = width
            new_height = height
        elif width:
            new_width = width
            # maintain aspect ratio
            new_height = round(new_width * img_height / img_width)
        elif height:
            new_height = height
            # maintain aspect ratio
            new_width = round(new_height * img_width / img_height)
        else:  # resize by %
            new_width = round(pil_image.size[0] * resize / 100)
            new_height = round(pil_image.size[1] * resize / 100)
        pil_image = pil_image.resize((new_width, new_height), resample=resample_filter)

    screenshot_filename = '{}.{}'.format(image_name, extension)
    screenshot_path = os.path.join(reportdir, screenshot_filename)
    if format == 'PNG':
        pil_image.save(screenshot_path, format=format, optimize=True)
    elif format == 'JPEG':
        if quality is None:
            pil_image.save(screenshot_path, format=format, optimize=True)
        else:
            pil_image.save(screenshot_path, format=format, optimize=True,
                           quality=quality)

    return screenshot_filename


def get_valid_filename(s):
    """Receives a string and returns a valid filename"""
    s = str(s).strip().replace(' ', '_')
    return re.sub(r'(?u)[^-\w.]', '', s)


def extract_version_from_webdriver_filename(filename):
    """Extract version from webdriver filename.

    Expects a file in the format: `filename_1.2` or `filename_1.2.exe`
    The extracted version must conform with pep-386
    If a valid version is not found it returns '0.0'
    """
    version = '0.0'
    if '_' in filename:
        components = filename.replace('.exe', '').split('_')
        if len(components) > 1:
            parsed_version = components[-1]
            try:
                StrictVersion(parsed_version)
                version = parsed_version
            except:
                pass
    return version


def match_latest_executable_path(glob_path):
    """Returns the absolute path to the webdriver executable
    with the highest version given a path with glob pattern.
    """
    found_files = []
    glob_path = os.path.normpath(glob_path)
    # Note: recursive=True arg is not supported
    # in Python 3.4, so '**' wildcard is not supported
    matched_files = [x for x in glob.glob(glob_path) if os.path.isfile(x)]
    for matched_file in matched_files:
        found_files.append((matched_file, extract_version_from_webdriver_filename(matched_file)))
    if found_files:
        highest_version = sorted(found_files, key=lambda tup: StrictVersion(tup[1]), reverse=True)
        return highest_version[0][0]
    else:
        return None

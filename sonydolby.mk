#
# SPDX-FileCopyrightText: The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from the proprietary version
$(call inherit-product, vendor/sony/dolby/dolby-vendor.mk)

DOLBY_PATH := vendor/sony/dolby

# Configs
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/configs/dolby/dax-default.xml:$(TARGET_COPY_OUT_VENDOR)/etc/dolby/dax-default.xml

# LunarisDolby
$(call inherit-product, packages/apps/LunarisDolby/lunarisdolby.mk)

# Overlay-RRO
PRODUCT_PACKAGES += \
    SonyDolbyResCommon

# Permissions
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/configs/permissions/privapp-permissions-dolby.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/privapp-permissions-dolby.xml

# Sepolicy
BOARD_VENDOR_SEPOLICY_DIRS += $(DOLBY_PATH)/sepolicy/vendor

# Spatial Audio permissions
PRODUCT_COPY_FILES += \
    $(DOLBY_PATH)/configs/permissions/android.hardware.sensor.dynamic.head_tracker.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.dynamic.head_tracker.xml \

# Spatial Audio: optimize spatializer effect
PRODUCT_PROPERTY_OVERRIDES += \
       audio.spatializer.effect.util_clamp_min=300

# Spatial Audio: declare use of spatial audio
PRODUCT_PROPERTY_OVERRIDES += \
       ro.audio.spatializer_enabled=true \
       ro.audio.headtracking_enabled=true \
       ro.audio.spatializer_transaural_enabled_default=false \
       persist.vendor.audio.spatializer.speaker_enabled=true

# Properties
TARGET_VENDOR_PROP += $(DOLBY_PATH)/vendor.prop

# VINTF
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE += $(DOLBY_PATH)/configs/hidl/dolby_framework_matrix.xml

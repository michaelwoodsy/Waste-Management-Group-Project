<template>
    <div :class="alertStyles" role="alert">
        <slot/>
    </div>
</template>

<script>
    export default {
        name: "Alert",
        props: {
            'alertType': {
                type: String,
                default: function () {
                    return 'alert-danger'
                }
            }
        },
        computed: {
            alertStyles () {
                return `alert ${this.alertType}`
            }
        }
    }
</script>

<style scoped>

</style>

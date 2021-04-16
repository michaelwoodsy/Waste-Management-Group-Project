<template>
  <a class="pointer" @click="go">
    <slot/>
  </a>
</template>

<script>
export default {
  name: "RouterLink",
  props: {
    to: [String, Object]
  },
  computed: {
    /** The 'to' prop with the base url added to the path **/
    toWithBase() {
      let newTo = this.to
      if (typeof this.to === 'object') {
        // Update the path param with the base url, if the path param provided
        if (newTo.path) {
          newTo.path = this.$router.base + this.withoutBaseSlash(newTo.path)
        }
      } else {
        // 'to' must be a string, add the base url
        newTo = this.$router.base + this.withoutBaseSlash(newTo)
      }
      return newTo
    }
  },
  methods: {
    /** Goes to the routed specified in 'to' **/
    go() {
      this.$router.push(this.toWithBase)
    },

    /**
     * Removes the starting slash from a path if there is one
     * @param str Path to remove the starting slash from
     * @returns {*} Path without the stating slash
     */
    withoutBaseSlash(str) {
      return str[0] === '/' ? str.slice(1) : str
    }
  }
}
</script>

<style scoped>

</style>

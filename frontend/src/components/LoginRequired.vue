<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col-12 text-center mb-2">
                <h4>Login Required</h4>
            </div>
        </div>
      <!-- User is not logged in-->
        <div class="row">
            <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">
                <p class="text-muted">You must be logged in to {{page}}.</p>
                <div class="form-row">
                    <div class="form-group col-6">
                      <!-- Register Button -->
                        <router-link to="/register" class="no-underline">
                            <button type="button" class="btn btn-block btn-light">Register</button>
                        </router-link>
                    </div>
                    <div class="form-group col-6">
                      <!-- Login Button -->
                        <router-link to="/login" class="no-underline">
                            <button class="btn btn-block btn-primary">Login</button>
                        </router-link>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<script>
    export default {
        name: "LoginRequired",
        props: ['page']
    }
</script>

<style scoped>
.no-underline {
    text-decoration: none;
}
</style>

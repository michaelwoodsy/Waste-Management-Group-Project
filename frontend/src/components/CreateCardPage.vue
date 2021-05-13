<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>{{ PageTitle }}</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
      <!-- Section -->
      <div class="form-group row">
        <label for="section"><b>Section<span class="required">*</span></b></label>
          <select id="section" v-model="section" :class="{'form-control': true, 'is-invalid': msg.section}" >
            <option>
              For Sale
            </option>
            <option>
              Wanted
            </option>
            <option>
              Exchange
            </option>
          </select>
        <span class="invalid-feedback">{{ msg.section }}</span>
      </div>

      <!-- Title -->
      <div class="form-group row">
        <label for="title"><b>Card Title<span class="required">*</span></b></label>
        <input id="title" v-model="title" :class="{'form-control': true, 'is-invalid': msg.title}"
               placeholder="Enter the title"
               required maxlength="255" type="text">
        <span class="invalid-feedback">{{ msg.title }}</span>
      </div>

      <!-- Description -->
      <div class="form-group row">
        <label for="description"><b>Description</b></label>
        <input id="description" v-model="description" :class="{'form-control': true, 'is-invalid': msg.description}"
               placeholder="Enter the description"
               required maxlength="255" type="text">
        <span class="invalid-feedback">{{ msg.description }}</span>
      </div>

      <!-- Create Card button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <router-link :to="'marketplace'" class="btn btn-secondary col-4">Cancel
          </router-link>
          <button class="btn btn-primary col-8" @click="checkInputs">Create Card</button>
        </div>
        <!-- Show an error if required fields are missing -->
        <div v-if="msg.errorChecks" class="error-box">
          <alert class="mb-0">{{ msg.errorChecks }}</alert>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Alert from "@/components/Alert";

export default {
  name: "CreateCardPage",
  components: {Alert},
  data() {
    return {
      PageTitle: 'Create a new card',
      creatorId: null,
      section: null,
      title: null,
      description: null,
      keywordIds: null,
      msg: {
        section: null,
        title: null,
        description: null,
        keywordIds: null,
        errorChecks: null
      },
      valid: true,
    };
  },
  methods: {
    /**
     * Validate the section input
     */
    validateSection(){
      if (this.section === '' || this.section === null){
        this.msg.section = 'Please select a section'
        this.valid = false
      } else if (this.section === 'For Sale' || this.section === 'Wanted' || this.section === 'Exchange'){
        this.msg.section = null
      } else {
        this.msg.section = 'Please select an appropriate section'
        this.valid = false
      }
    },
    /**
     * Validate the title input
     */
    validateTitle(){
      if (this.title === '' || this.title === null){
        this.msg.title = 'Please enter a title'
        this.valid = false
      } else {
        this.msg.title = null
      }
    },


    /**
     * Check all inputs
     */
    checkInputs(){
      this.validateSection()
      this.validateTitle()

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
      }
    }
  }
}
</script>

<style scoped>

.required {
  color: red;
}

.form-group {
  margin-bottom: 30px;
}

.error-box {
  width: 100%;
  margin: 20px;
  text-align: center;
}

</style>
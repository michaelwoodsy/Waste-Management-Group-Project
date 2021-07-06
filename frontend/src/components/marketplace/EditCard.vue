<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>{{ modalTitle }}</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
      <!-- Section -->
      <div class="form-group row">
        <label for="section"><strong>Section<span class="required">*</span></strong></label>
        <select id="section" v-model="section" :class="{'form-control': true, 'is-invalid': msg.section}" >
          <option value="ForSale">
            For Sale
          </option>
          <option value="Wanted">
            Wanted
          </option>
          <option value="Exchange">
            Exchange
          </option>
        </select>
        <span class="invalid-feedback">{{ msg.section }}</span>
      </div>

      <!-- Title -->
      <div class="form-group row">
        <label for="title"><strong>Card Title<span class="required">*</span></strong></label>
        <input id="title" v-model="title" :class="{'form-control': true, 'is-invalid': msg.title}"
               placeholder="Enter the title"
               required maxlength="255" type="text">
        <span class="invalid-feedback">{{ msg.title }}</span>
      </div>

      <!-- Description -->
      <div class="form-group row">
        <label for="description"><strong>Description</strong></label>
        <textarea id="description" v-model="description" :class="{'form-control': true, 'is-invalid': false}"
                  placeholder="Enter the description"
                  required maxlength="255" type="text">
        </textarea>
      </div>

      <!-- Keywords -->
      <div class="form-group row">
        <label for="keywordValue">
          <strong>Keywords<span class="required">*</span></strong>
          (Keywords must be 25 characters or less)
        </label>
        <!-- Keyword Input -->
        <input id="keywordValue" v-model="keywordValue"
               :class="{'form-control': true, 'is-invalid': msg.keywords}"
               placeholder="Enter the Keywords"
               required maxlength="25" type="text"
               style="margin-bottom: 2px"
               autocomplete="off"
               data-toggle="dropdown"
               @input="searchKeywords"
               @keyup.space="addKeyword"/>
        <!-- Autocomplete dropdown -->
        <div class="dropdown-menu overflow-auto" id="dropdown">
          <!-- If no user input -->
          <p class="text-muted dropdown-item left-padding mb-0 disabled"
             v-if="keywordValue.length === 0"
          >
            Start typing...
          </p>
          <!-- If no matches -->
          <p class="text-muted dropdown-item left-padding mb-0 disabled"
             v-else-if="filteredKeywords.length === 0 && keywordValue.length > 0"
          >
            No results found.
          </p>
          <!-- If there are matches -->
          <a class="dropdown-item pointer left-padding"
             v-for="keyword in filteredKeywords"
             v-else
             :key="keyword.id"
             @click="setKeyword(keyword.name)">
            <span>{{ keyword.name }}</span>
          </a>
        </div>
        <!-- Keyword Bubbles -->
        <div class="keyword" v-for="(keyword, index) in keywords" style="padding: 2px"
             :key="'keyword' + index">
          <button class="btn btn-primary">
            <span>{{  keyword  }}</span>
            <span @click="removeKeyword(index)"><em class="bi bi-x"></em></span>
          </button>
        </div>
        <span class="invalid-feedback">{{ msg.keywords }}</span>
      </div>

      <!-- Save Changes button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal" v-on:click="cancel=true" @click="close">Cancel</button>
          <button id="saveButton" class="btn btn-primary col-8" v-on:click="submit=true" @click="checkInputs">Save Changes</button>
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
import {Card, Keyword} from "@/Api";

export default {
  name: "EditCard",
  components: {
    Alert,
  },
  props: ['cardId'],
  data() {
    return {
      modalTitle: 'Edit card',
      section: '', //Required
      title: '', //Required
      description: '',
      msg: {
        section: null,
        title: null,
        keywords: null,
        errorChecks: null
      },
      valid: true,
      cancel: false,
      submit: false,
      keywordValue: '',
      keywords: [],
      filteredKeywords: []
    };
  },
  async mounted() {
    await this.prefillFields();
  },
  methods: {
    /**
     * Prefills all fields with the card's existing values
     */
    async prefillFields() {
      const response = await Card.getCard(this.cardId)
      this.section = response.data.section
      this.title = response.data.title
      this.description = response.data.description
      response.data.keywords.forEach((keywordObject) => {
        this.keywords.push(keywordObject.name)
      })


    },
    /**
     * Validate the section input
     * Must select one of the 3 available sections
     */
    validateSection(){
      if (this.section === '' || this.section === null){
        this.msg.section = 'Please select a section'
        this.valid = false
      } else if (this.section === 'ForSale' || this.section === 'Wanted' || this.section === 'Exchange'){
        this.msg.section = null
      } else {
        this.msg.section = 'Please select an appropriate section'
        this.valid = false
      }
    },
    /**
     * Validate the title input
     * Cannot be empty
     */
    validateTitle(){
      if (this.title != null) {
        this.title = this.title.trim()
      }
      if (this.title === '' || this.title === null){
        this.msg.title = 'Please enter a title'
        this.valid = false
      } else {
        this.msg.title = null
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise add card to marketplace
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
        this.editCard()
      }
    },

    /**
     * Gets the keyword IDs for the keywords of a card.
     * Creates a new keyword if one doesn't exist.
     */
    async getKeywordIds() {
      const keywordIds = []
      for (const keyword of this.keywords) {
        const response = await Keyword.searchKeywords(keyword)
        if (response.data.length === 0) {
          const keywordId = (await Keyword.createKeyword(keyword)).data['keywordId']
          keywordIds.push(keywordId)
        } else {
          for (const result of response.data) {
            if (result['name'] === keyword) {
              keywordIds.push(result['id'])
            }
          }
        }
      }
      return keywordIds
    },

    /**
     * Saves the changes from editing the card
     */
    async editCard() {
      const keywordIds = await this.getKeywordIds()
      await Card.editCard(this.cardId, {
        "section": this.section,
        "title": this.title,
        "description": this.description,
        "keywordIds": keywordIds
      })
    },

    /**
     * Closes the popup window to create a card
     */
    close() {
      this.$emit('refresh-cards');
    },

    /**
     * Adds a keyword to the list of keywords
     */
    addKeyword() {
      this.keywordValue = this.keywordValue.trim()
      if((this.keywordValue === '' || this.keywordValue === ' ')
          || this.keywords.includes(this.keywordValue)) {
        this.keywordValue = '';
      }
      if (this.keywordValue.length > 2) {
        this.keywords.push(this.keywordValue);
        this.keywordValue = '';
      }
    },

    /**
     * Removes a keyword from the list of keywords
     * @param index Index of the keyword in the keyword list
     */
    removeKeyword(index) {
      this.keywords.splice(index, 1)
    },

    /**
     * Filters autocomplete options based on the user's input for a keyword.
     */
    async searchKeywords() {
      if (this.keywordValue.length > 2) {
        await Keyword.searchKeywords(this.keywordValue)
            .then((response) => {
              this.filteredKeywords = response.data;
            })
            .catch((err) => {
              this.msg.errorChecks = err.response
                  ? err.response.data.slice(err.response.data.indexOf(":") + 2)
                  : err

            })
      } else {
        this.filteredKeywords = []
      }
    },

    /**
     * Adds a keyword to the list of keywords if the keyword was selecting from the
     * autocomplete list rather than by pressing the spacebar
     * @param keyword Keyword to be added to keyword list
     */
    setKeyword(keyword) {
      this.keywordValue = keyword
      this.addKeyword()
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
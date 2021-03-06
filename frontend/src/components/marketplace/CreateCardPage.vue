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
        <label for="section"><strong>Section<span class="required">*</span></strong></label>
        <select id="section" v-model="section" :class="{'form-control': true, 'is-invalid': msg.section}">
          <option disabled hidden selected value>Please select one</option>
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
               maxlength="255"
               placeholder="Enter the title" required type="text">
        <span class="invalid-feedback">{{ msg.title }}</span>
      </div>

      <!-- Description -->
      <div class="form-group row">
        <label for="description"><strong>Description</strong></label>
        <textarea id="description" v-model="description" :class="{'form-control': true, 'is-invalid': false}"
                  maxlength="255"
                  placeholder="Enter the description" required type="text">
        </textarea>
      </div>

      <!-- Keywords -->
      <div class="form-group row">
        <label for="keywordValue">
          <strong>Keywords</strong>
          (Keywords must be between 3 and 25 characters)
        </label>
        <!-- Keyword Input -->
        <input id="keywordValue" v-model="keywordValue"
               :class="{'form-control': true, 'is-invalid': msg.keywords}"
               autocomplete="off"
               data-toggle="dropdown" maxlength="25" placeholder="Enter the Keywords"
               required
               style="margin-bottom: 2px"
               type="text"
               @click="showAutocomplete"
               @input="searchKeywords"
               @keyup.space="addKeyword"
               @keyup.enter="setKeyword()"/>
        <!-- Autocomplete dropdown -->
        <div id="selectKeywordDropdown" class="dropdown-menu overflow-auto">
          <!-- If no user input -->
          <p v-if="keywordValue.length === 0"
             class="text-muted dropdown-item left-padding mb-0 disabled"
          >
            Start typing...
          </p>
          <!-- If no matches -->
          <p v-else-if="filteredKeywords.length === 0 && keywordValue.length > 0"
             class="text-muted dropdown-item left-padding mb-0 disabled"
          >
            No results found.
          </p>
          <!-- If there are matches -->
          <a v-for="keyword in filteredKeywords" v-else
             :key="keyword.id"
             class="dropdown-item pointer left-padding"
             href="#"
             @click="setKeyword(keyword.name)">
            <span>{{ keyword.name }}</span>
          </a>
        </div>
        <!-- Keyword Bubbles -->
        <div class="mt-2">
          <h4 v-for="(keyword, index) in keywords" :key="'keyword' + index" class="float-left mr-2 mb-2">
              <span class="badge badge-primary" style="font-size: medium; cursor: default">
                {{ keyword }}
                <span style="cursor: pointer" @click="removeKeyword(index)"><em class="bi bi-x"/></span>
              </span>
          </h4>
        </div>
        <span class="invalid-feedback">{{ msg.keywords }}</span>
      </div>

      <!-- Create Card button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal"
                  @click="close" v-on:click="cancel=true">Cancel
          </button>
          <button id="createButton" class="btn btn-primary col-8" @click="checkInputs" v-on:click="submit=true">Create
            Card
          </button>
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
import {Keyword} from "@/Api";

export default {
  name: "CreateCardPage",
  components: {
    Alert,
  },
  data() {
    return {
      PageTitle: 'Create a new card',
      creatorId: '', //Required
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
  methods: {
    /**
     * Validate the section input
     * Must select one of the 3 available sections
     */
    validateSection() {
      if (this.section === '' || this.section === null) {
        this.msg.section = 'Please select a section'
        this.valid = false
      } else if (this.section === 'ForSale' || this.section === 'Wanted' || this.section === 'Exchange') {
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
    validateTitle() {
      if (this.title != null) {
        this.title = this.title.trim()
      }
      if (this.title === '' || this.title === null) {
        this.msg.title = 'Please enter a title'
        this.valid = false
      } else {
        this.msg.title = null
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise add card to marketplace
     */
    checkInputs() {
      this.validateSection()
      this.validateTitle()

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        this.addCard()
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
        //Filter to see if the keyword is already in the database
        const filterKeywords = response.data.filter(function (indKeyword) {
          return indKeyword.name === keyword;
        })
        //if keyword is not in database
        if (filterKeywords.length === 0) {
          const keywordId = (await Keyword.createKeyword(keyword)).data['keywordId']
          keywordIds.push(keywordId)
        } else {
          for (const result of response.data) {
            if (result['name'] === keyword) {
              keywordIds.push(result['id'])
              break
            }
          }
        }
      }
      return keywordIds
    },

    /**
     * Add a new card to the marketplace
     */
    async addCard() {
      const keywordIds = await this.getKeywordIds()
      this.$root.$data.user.createCard(
          {
            creatorId: this.$root.$data.user.state.actingAs.id,
            section: this.section,
            title: this.title,
            description: this.description,
            keywordIds: keywordIds
          }
      ).then(() => {
        this.$refs.close.click();
      }).catch((err) => {
        this.showError(err)
      });
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
      this.keywordValue = this.keywordValue.trim().toLowerCase()
      if ((this.keywordValue === '' || this.keywordValue === ' ')
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
              this.showError(err)
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
      //If enter was pressed on the input box to automatically select the first keyword in the list
      if (keyword === undefined && this.filteredKeywords.length > 0) {
        keyword = this.filteredKeywords[0].name
      }
      this.keywordValue = keyword
      this.addKeyword()
    },

    /**
     * Shows the error returned in a request
     * @param err error returned
     */
    showError(err) {
      this.msg.errorChecks = err.response
          ? err.response.data.slice(err.response.data.indexOf(":") + 2)
          : err
    },

    /**
     * Helper function to make sure autocomplete dropdown is not hidden when clicked on
     */
    showAutocomplete() {
      const dropdown = document.getElementById('selectKeywordDropdown')
      if (dropdown.classList.contains('show')) {
        document.getElementById('keywordValue').click()
      }
    },
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
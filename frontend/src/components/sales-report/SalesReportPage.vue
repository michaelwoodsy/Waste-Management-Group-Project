<template>
  <div>

    <h2>Sales Report</h2>

    <sales-report
        :data="report"
        :currency="currency"
    />

  </div>
</template>

<script>
import SalesReport from "@/components/sales-report/SalesReport";
import {Business} from "@/Api";
import product from "@/store/modules/product";
export default {
  name: "SalesReportPage",
  components: {SalesReport},
  props: {
    businessId: Number
  },
  data() {
    return {
      report: [ // TODO: Remove once report is retrieved from backend
        {
          periodStart: '2021-06-01',
          periodEnd: '2021-06-30',
          totalPurchaseValue: 50.00,
          purchaseCount: 4,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            }
          ]
        },
        {
          periodStart: '2021-07-01',
          periodEnd: '2021-07-31',
          totalPurchaseValue: 40.00,
          purchaseCount: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        },
        {
          periodStart: '2021-07-01',
          periodEnd: '2021-07-31',
          totalPurchaseValue: 40.00,
          purchaseCount: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        },
        {
          periodStart: '2021-07-01',
          periodEnd: '2021-07-31',
          totalPurchaseValue: 40.00,
          purchaseCount: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        }
      ],
      currency: null
    }
  },
  async mounted() {
    await this.getCurrency()
  },
  methods: {
    /**
     * Gets the currency of the business to use for the report
     */
    async getCurrency() {
      try {
        const business = (await Business.getBusinessData(this.businessId)).data
        this.currency = await product.getCurrency(business.address.country)
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>

</style>
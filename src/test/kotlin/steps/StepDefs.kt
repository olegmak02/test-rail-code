package steps

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

@SuppressWarnings("EmptyFunctionBlock", "UnusedParameter")
class StepDefs {
    // GIVENS
    @Given("I have {int} cukes in my belly")
    fun i_have_cukes_in_my_belly(int1: Int?) {}

    @Given("User is PRO")
    fun user_is_pro() {}

    // WHENS
    @When("I wait {int} hours")
    fun i_wait_hour(int1: Int?) {}

    // THENS
    @Then("my belly should growl")
    fun my_belly_should_growl() {}
}

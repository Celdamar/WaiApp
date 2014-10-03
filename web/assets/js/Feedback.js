(function() {
  var checkEmail, checkMessage, checkName, checkSubject, getEmail, getMessage, getName, getSubject, sendButton;

  sendButton = $("#sendButton");

  getName = function() {
    var nameInput;
    nameInput = $("#nameInput");
    return nameInput.val();
  };

  getSubject = function() {
    var subjectInput;
    subjectInput = $("#subjectInput");
    return subjectInput.val();
  };

  getMessage = function() {
    var messageInput;
    messageInput = $("#messageInput");
    return messageInput.val();
  };

  getEmail = function() {
    var emailInput;
    emailInput = $("#emailInput");
    return emailInput.val();
  };

  checkName = function() {
    var nameControlGroup;
    if (getName() === "") {
      nameControlGroup = $("#nameControlGroup");
      nameControlGroup.addClass("error");
      nameControlGroup.change(function() {
        if (getName() !== "") return nameControlGroup.removeClass("error");
      });
      return false;
    }
    return true;
  };

  checkSubject = function() {
    var controlGroup;
    if (getSubject() === "") {
      controlGroup = $("#subjectControlGroup");
      controlGroup.addClass("error");
      controlGroup.change(function() {
        if (getSubject() !== "") return controlGroup.removeClass("error");
      });
      return false;
    }
    return true;
  };

  checkMessage = function() {
    var controlGroup;
    if (getMessage() === "") {
      controlGroup = $("#messageControlGroup");
      controlGroup.addClass("error");
      controlGroup.change(function() {
        if (getMessage() !== "") return controlGroup.removeClass("error");
      });
      return false;
    }
    return true;
  };

  checkEmail = function() {
    var controlGroup;
    if (getEmail() === "") {
      controlGroup = $("#emailControlGroup");
      controlGroup.addClass("error");
      controlGroup.change(function() {
        if (getEmail() !== "") return controlGroup.removeClass("error");
      });
      return false;
    }
    return true;
  };

  sendButton.click(function() {
    var allCorrect, args, callType, data, onFailure, onSuccess, path;
    allCorrect = true;
    allCorrect = checkName() && allCorrect;
    allCorrect = checkSubject() && allCorrect;
    allCorrect = checkEmail() && allCorrect;
    allCorrect = checkMessage() && allCorrect;
    if (allCorrect) {
      console.log("All fields are fine");
      data = {
        name: getName(),
        subject: getSubject(),
        email: getEmail(),
        message: getMessage()
      };
      onSuccess = function(msg) {
        sendButton.removeClass("btn-primary");
        sendButton.addClass("btn-success");
        console.log("Success");
        return sendButton.text("Feedback Submitted Successfuly");
      };
      onFailure = function(msg) {
        sendButton.removeClass("btn-primary");
        sendButton.addClass("btn-danger");
        console.log("Fail");
        return sendButton.text("Feedback Submission Failed");
      };
      path = "/Submit/Feedback";
      args = [];
      callType = "POST";
      sendButton.text("Submitting...");
      window.RWCall(onSuccess, onFailure, data, path, args, callType);
      return false;
    }
    return false;
  });

}).call(this);

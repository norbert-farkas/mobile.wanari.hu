package controllers;

import static play.data.Form.form;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.advertapps;
import views.html.corpapps;
import views.html.index;
import views.html.mobapps;
import views.html.startupapps;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class Application extends Controller {

	private static final Form<ContactForm> FORM = form(ContactForm.class);

	public static Result index() {
		String url = routes.Application.index().absoluteURL(request());
		if(url.contains(".com")) {
			Language.setLang(Language.LANGUAGE_EN);
		} else {
			Language.setLang(Language.LANGUAGE_HU);
		}
		changeLang(Language.getLang());
		return ok(index.render());
	}

	public static Result firstPage() {
		return ok(mobapps.render(form(ContactForm.class)));
	}

	public static Result secondPage() {
		return ok(startupapps.render(form(ContactForm.class)));
	}

	public static Result thirdPage() {
		return ok(advertapps.render(form(ContactForm.class)));
	}

	public static Result fourthPage() {
		return ok(corpapps.render(form(ContactForm.class)));
	}

	public static Result sendMail() {
		Form<ContactForm> form = FORM.bindFromRequest();
		if(!form.hasErrors()) {
			MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
			mail.setSubject("[WANARI] Jelentkezés");
			mail.setRecipient(Play.application().configuration().getString("mail.to"));
			mail.setBcc(Play.application().configuration().getString("mail.bcc"));
			mail.setFrom(form.get().name + "<" + form.get().email + ">");
			StringBuffer message = new StringBuffer();
			message.append("\nTéma:\n");
			message.append(form.get().topic);
			message.append("\n\nCsatorna:\n");
			message.append(form.get().channel);
			message.append("\n\nÜzenet:\n");
			message.append(form.get().message);
			message.append("\n\n");
			message.append("Feladó:\n");
			message.append("Név: ");
			message.append(form.get().name);
			message.append("\n");
			message.append("E-mail: ");
			message.append(form.get().email);
			if(!form.get().phonenumber.equals("")) {
				message.append("\n");
				message.append("Telefonszám: ");
				message.append(form.get().phonenumber);
			}
			message.append("\n");
			mail.send(message.toString());
			flash("success", "E-mail elküldve, köszönjük!");
			return redirect(routes.Application.index());
		} else {
			flash("error", "Kérlek javítsd a hibákat!");
			return redirect(routes.Application.index());
		}

	}

	public static class ContactForm {
		@Required
		public String name;

		@Required
		public String email;

		public String phonenumber;

		public String channel;

		public String topic;

		@Required
		public String message;
	}
}

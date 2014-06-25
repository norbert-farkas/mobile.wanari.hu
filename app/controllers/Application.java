package controllers;

import static play.data.Form.form;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.advert;
import views.html.enterprise;
import views.html.index;
import views.html.general;
import views.html.startup;

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

	public static Result general() {
		return ok(general.render(form(ContactForm.class)));
	}

	public static Result startup() {
		return ok(startup.render(form(ContactForm.class)));
	}

	public static Result advert() {
		return ok(advert.render(form(ContactForm.class)));
	}

	public static Result enterprise() {
		return ok(enterprise.render(form(ContactForm.class)));
	}

	public static Result sendMail() {
		try {
			Form<ContactForm> form = FORM.bindFromRequest();
			if(!form.hasErrors()) {
				MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
				mail.setSubject("[WANARI] Jelentkezés");
				mail.setRecipient(Play.application().configuration().getString("mail.to"));
				mail.setBcc(Play.application().configuration().getString("mail.bcc"));
				if(!form.get().email.equals("")) {
					mail.setFrom(form.get().name + "<" + form.get().email + ">");
				} else {
					mail.setFrom(form.get().name + "<" + "job@wanari.com" + ">");
				}
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
				if(!form.get().email.equals("")) {
					message.append("\n");
					message.append("E-mail: ");
					message.append(form.get().email);
				}
				if(!form.get().phonenumber.equals("")) {
					message.append("\n");
					message.append("Telefonszám: ");
					message.append(form.get().phonenumber);
				}
				message.append("\n");
				mail.send(message.toString());
				flash("success", Messages.get("mail.success"));
				return redirect(routes.Application.index());
			} else {
				flash("error", Messages.get("mail.error"));
				return redirect(routes.Application.index());
			}
		} catch(Exception e) {
			flash("error", Messages.get("mail.error.2"));
			return redirect(routes.Application.index());
		}
	}

	public static class ContactForm {
		@Required
		public String name;

		public String email;

		public String phonenumber;

		public String channel;

		public String topic;

		@Required
		public String message;
	}
}

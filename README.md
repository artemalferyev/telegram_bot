Here’s a sample README file for the Kupidon Telegram bot based on the code you’ve provided:

Kupidon Telegram Bot

Kupidon is a Telegram bot designed to help users with the process of purchasing goods from the USA, Europe, and Dubai, with features like item ordering, delivery information, and more. It is powered by Spring Boot and uses the Telegram Bot API to provide interactive user experiences.

Features
	•	Welcome Message: A personalized greeting for each user when they start interacting with the bot.
	•	Product Delivery Info: Provides information about delivery services, including free storage, customs limits, and more.
	•	Interactive Buttons: Allows users to interact with various options such as ordering, catalog browsing, and feedback via inline keyboard buttons.
	•	Photo Sharing: Sends images related to products or delivery information in response to user actions.
	•	Multiple Regions: Support for choosing delivery regions (Europe and USA) with region-specific information and images.

Technologies Used
	•	Java 21
	•	Spring Boot
	•	Telegram Bot API
	•	Lombok
	•	Telegram Bots Java Library

Prerequisites
	•	Java 21
	•	Maven
	•	Telegram Bot Token

Installation
	1.	Clone the repository to your local machine:

git clone https://github.com/yourusername/kupidon-telegram-bot.git
cd kupidon-telegram-bot

	2.	Build the project using Maven:

mvn clean install

	3.	Set your Telegram Bot Token:
	•	Create a .env file in the root directory and add the bot token:

TELEGRAM_BOT_TOKEN=your-bot-token-here


	4.	Run the application:

mvn spring-boot:run

The bot will start and connect to Telegram, ready to accept user commands.

Configuration

Bot Configuration (application.properties)

You can configure the bot’s name and token by modifying the application.properties file:

spring.application.name=telegram_bot

bot.name=kupidonbuyerservice_bot
bot.token=${TELEGRAM_BOT_TOKEN}

Make sure to replace ${TELEGRAM_BOT_TOKEN} with the actual token in your environment variable or .env file.

Resources (Images)

Ensure that you have the image files in the resources folder for use by the bot when sending photos to the users. Example file paths:
	•	resources/photo_2025-01-17 15.47.06.jpeg
	•	resources/photo_2025-01-17 15.47.09.jpeg
	•	resources/photo_2025-01-17 15.47.11.jpeg

Commands
	•	/start: Sends a welcome message with options for ordering, catalog browsing, delivery information, and feedback.
	•	delivery: Sends detailed delivery information and options to choose a delivery region (Europe or USA).
	•	europe: Sends product photos and information for Europe.
	•	usa: Sends product photos and information for the USA.

Example Usage
	1.	Start the bot: Send /start to receive a greeting message from Kupidon Bot.
	2.	Explore delivery info: Send delivery to get information about delivery services, including photos.
	3.	Choose a region: Use the inline keyboard to choose either Europe or USA and view related product photos.

Bot Flow
	1.	When a user starts the bot with /start, they will receive a welcome message introducing Kupidon and its services.
	2.	The bot offers options like Order, Catalog, Delivery, and Feedback via inline buttons.
	3.	When the user selects Delivery, they will be presented with detailed information about delivery and storage, along with region options (Europe or USA).
	4.	Upon selecting a region, the bot sends corresponding photos of products from that region.

Troubleshooting
	•	Ensure that your Telegram bot token is valid.
	•	Ensure that images in the resources folder are available and correctly named.
	•	If the bot doesn’t respond to certain commands, check the logs for errors in the application.properties file or class configuration.

License

This project is licensed under the MIT License - see the LICENSE file for details.

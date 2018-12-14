//
//  UserViewController.swift
//  TravelSample
//
//  Created by Priya Rajagopal on 12/15/17.
//  Copyright © 2017 Couchbase Inc. All rights reserved.
//

import UIKit



class UserTableViewController:UITableViewController ,PresentingViewProtocol{
    lazy var userProfilePresenter:UserPresenter = UserPresenter()
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var userImageView: UIImageView!
    @IBOutlet weak var saveButtonItem: UIBarButtonItem!
    
    var userProfile:User?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        self.title = NSLocalizedString("User Profile", comment: "")
        
        self.initializeTable()
        
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.loadUserProfile()
        
        
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(true)
     }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    private func initializeTable() {
        //    self.tableView.backgroundColor = UIColor.darkGray
        self.tableView.backgroundColor = UIColor(red: 252.0/255, green: 252.0/255, blue: 252.0/255, alpha: 1.0)
        
    }
    
    
    
}

// MARL: UI Updates
extension UserTableViewController {
    public func updateUIWithFetchedUserData() {
        guard let userProfile = userProfile else {return}
        
        self.nameLabel.text = userProfile["username"] as? String
        
        
        if let profileImagData  = userProfile["imageProfile"] as? Data {
            if let profileImage = UIImage.init(data: profileImagData)  {
                self.userImageView.image = profileImage
            }
        }
       
        
    }
}
// MARK: IN Actions
extension UserTableViewController {
    @IBAction func updateThumbnail(_ sender: UIButton) {
        
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        alert.modalPresentationStyle = .popover
        
        let albumAction = UIAlertAction(title: NSLocalizedString("Select From Photo Album", comment: ""), style: .default) { action in
            
            let imagePickerController = UIImagePickerController()
            imagePickerController.delegate = self
            imagePickerController.allowsEditing = false
            imagePickerController.sourceType = .photoLibrary;
            
            imagePickerController.modalPresentationStyle = .overCurrentContext
            
            self.present(imagePickerController, animated: true, completion: nil)
            
        }
        
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            let cameraAction = UIAlertAction(title: NSLocalizedString("Take Photo", comment: ""), style: .default) { [unowned self] action in
                
                let imagePickerController = UIImagePickerController()
                imagePickerController.delegate = self
                imagePickerController.allowsEditing = false
                imagePickerController.sourceType = .camera;
                imagePickerController.cameraDevice = .front;
                
                imagePickerController.modalPresentationStyle = .overCurrentContext
                
                self.present(imagePickerController, animated: true, completion: nil)
                
                
            }
            alert.addAction(cameraAction)
            
        }
        alert.addAction(albumAction)
        
        if let presenter = alert.popoverPresentationController {
            presenter.sourceView = sender
            presenter.sourceRect = sender.bounds
        }
        present(alert, animated: true, completion: nil)
        
        
    }
    
    @IBAction func cancelTapped(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)

    }
    @IBAction func updateTapped(_ sender: UIBarButtonItem) {
        guard userProfile != nil else {return}
        guard let image = userImageView.image else {return}
        
        self.userProfilePresenter.updateProfileImage(image) { (error) in
            switch error {
            case nil:
                self.showAlertWithTitle(NSLocalizedString("Success!", comment: ""), message: NSLocalizedString("Succesfully updated user image!", comment: ""))
                
            default:
                self.showAlertWithTitle(NSLocalizedString("Error!", comment: ""), message: NSLocalizedString("Failed to update user image. Error code :\((error as NSError?)?.code ?? 0)!", comment: ""))
            }
        }
    }
    
}

extension UserTableViewController : UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if let image = info[.originalImage] as? UIImage {
            self.saveButtonItem.isEnabled = true
            self.userImageView.image = image
 
            picker.presentingViewController?.dismiss(animated: true, completion: nil)
        }
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.saveButtonItem.isEnabled = false
        picker.presentingViewController?.dismiss(animated: true, completion: nil)
    }
    
}

// Private
extension UserTableViewController {
    func loadUserProfile() {
        self.userProfilePresenter.fetchLoggedInUserProfile { [weak self](user, error) in
            guard let `self` = self else {
                return
            }
            switch error {
            case nil:
                self.userProfile = user
                self.updateUIWithFetchedUserData()
              
            default:
              
                self.showAlertWithTitle(NSLocalizedString("Error!", comment: ""), message: NSLocalizedString("There was an error when trying to load user data. Error code \((error as NSError?)?.code ?? 0)!", comment: ""))
            }
        }
        
    }
    
}




package com.atsushini.hedgedocportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.authentication.AuthenticationUtil;
import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.RuleDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.entity.Rule;
import com.atsushini.hedgedocportal.entity.User;
import com.atsushini.hedgedocportal.exception.ForbiddenException;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.repository.FolderRepository;
import com.atsushini.hedgedocportal.repository.RuleRepository;
import com.atsushini.hedgedocportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleService {
    
    private final RuleRepository ruleRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public List<RuleDto> getRules() {
        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        List<Rule> ruleDtos = ruleRepository.findByUserId(currentUserDto.getId());

        return ruleDtos.stream().map(this::convertToDto).toList();
    }

    // 振り分けルールを作成する
    public void create(String title, String reqularExpression, Long folderId) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null) throw new NotFoundException("Folder not found with ID: " + folderId);        

        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        if (!folder.getUser().getId().equals(currentUserDto.getId())) {
            throw new ForbiddenException();
        }

        Rule rule = new Rule();
        rule.setTitle(title);
        rule.setRegularExpression(reqularExpression);
        rule.setFolder(folder);

        User user = userRepository.findById(currentUserDto.getId()).orElse(null);
        rule.setUser(user);

        ruleRepository.save(rule);
    }

    // 振り分けルールを更新する
    public void update(Long id, String title, String regularExpression, Long folderId) {
        UserDto currenUserDto = AuthenticationUtil.getCurrentUser();
        Rule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) throw new NotFoundException("Rule not found with ID: " + id);
        if (!rule.getUser().getId().equals(currenUserDto.getId())) {
            throw new ForbiddenException();
        }

        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null) throw new NotFoundException("Folder not found with ID: " + folderId);        
        if (!folder.getUser().getId().equals(currenUserDto.getId())) {
            throw new ForbiddenException();
        }

        rule.setTitle(title);
        rule.setRegularExpression(regularExpression);
        rule.setFolder(folder);

        ruleRepository.save(rule);
    }

    // 振り分けルールを削除する
    public void delete(Long id) {
        Rule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) throw new NotFoundException("Rule not found with ID: " + id);

        UserDto currenUserDto = AuthenticationUtil.getCurrentUser();
        if (!rule.getUser().getId().equals(currenUserDto.getId())) {
            throw new ForbiddenException();
        }

        ruleRepository.delete(rule);
    }

    private RuleDto convertToDto(Rule rule) {
        RuleDto dto = new RuleDto();
        dto.setId(rule.getId());
        dto.setTitle(rule.getTitle());
        dto.setRegularExpression(rule.getRegularExpression());

        FolderDto folder = new FolderDto();
        folder.setId(rule.getFolder().getId());
        folder.setTitle(rule.getFolder().getTitle());
        dto.setFolder(folder);

        return dto;
    }
}
